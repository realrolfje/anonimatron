package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.anonymizer.DutchBSNAnononymizer;
import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.DataFile;
import com.rolfje.anonimatron.file.*;
import junit.framework.TestCase;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.assertNotEquals;

public class XMLFileAnonymizerServiceTest extends TestCase  {
    private XMLFileAnonymizerService xmlFileAnonymizerService;
    private AnonymizerService anonymizerService;

    public void setUp() throws Exception {
        super.setUp();

        Configuration config = new Configuration();
        anonymizerService = new AnonymizerService();
        anonymizerService.registerAnonymizers(config.getAnonymizerClasses());
        xmlFileAnonymizerService = new XMLFileAnonymizerService(config, anonymizerService);
    }

    // Houd nog even column, voor XML file moet dat eventueel field worden ...

    public void testAnonymize() {
        Element root = new Element("root");
        Element element = new Element("bsn");
        element.addContent("ABC");
        root.addContent(element);

        HashMap<String, Column> nameTypeMap = new HashMap<String, Column>();
        nameTypeMap.put("bsn", new Column("bsn", new DutchBSNAnononymizer().getType(), 50));

        Element anonymize = xmlFileAnonymizerService.anonymize(root, nameTypeMap);

        assertEquals(root.getName(), anonymize.getName());
        assertNotEquals(element.getValue(), anonymize.getChild("bsn").getValue());

        assertEquals(1, anonymizerService.getSynonymCache().size());
    }

    public void testGetInputFilesNonExisting() {
//		Path tempDirectory = Files.createTempDirectory("anonimatron-expand-test");
//		Files.createTempFile(tempDirectory, "a", ".txt");
//		Files.createTempFile(tempDirectory, "b", ".txt");

        DataFile dataFile = new DataFile();
        dataFile.setInFile(UUID.randomUUID().toString());
        try {
            xmlFileAnonymizerService.getInputFiles(dataFile);
            fail("Should throw exception, file should not exist.");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains(dataFile.getInFile()));
            assertTrue(e.getMessage().contains("does not exist"));
        }
    }

    public void testGetInputFilesDirectory() throws IOException {
        Path tempDirectory = Files.createTempDirectory("anonimatron-expand-test");
        Path file_a = Files.createTempFile(tempDirectory, "a", ".txt");
        Path file_b = Files.createTempFile(tempDirectory, "b", ".txt");

        DataFile dataFile = new DataFile();
        dataFile.setInFile(tempDirectory.toFile().getAbsolutePath());
        try {
            List<File> inputFiles = xmlFileAnonymizerService.getInputFiles(dataFile);
            assertEquals(2, inputFiles.size());
        } finally {
            assertTrue(Files.deleteIfExists(file_a));
            assertTrue(Files.deleteIfExists(file_b));
            assertTrue(Files.deleteIfExists(tempDirectory));
        }
    }

    public void testNoExceptionOnEmptyConfig() throws Exception {
        xmlFileAnonymizerService.anonymize();
    }

    public void testGetInputFilesSingleFile() throws IOException {
        Path file_a = Files.createTempFile("XMLFileAnonymizerTest", ".xml");

        DataFile dataFile = new DataFile();
        dataFile.setInFile(file_a.toFile().getAbsolutePath());
        try {
            List<File> inputFiles = xmlFileAnonymizerService.getInputFiles(dataFile);
            assertEquals(1, inputFiles.size());
        } finally {
            assertTrue(Files.deleteIfExists(file_a));
        }
    }

    public void testPreventCollisions() {
        try {
            xmlFileAnonymizerService.preventDataFileCollisions(Arrays.asList(
                    createDataFile("a", "a")
            ));
        } catch (RuntimeException e) {
            assertEquals("File used as both input and output: a.", e.getMessage());
        }

        try {
            xmlFileAnonymizerService.preventDataFileCollisions(Arrays.asList(
                    createDataFile("a", "b"),
                    createDataFile("b", "c")
            ));
        } catch (RuntimeException e) {
            assertEquals("Configuration will overwrite input file b.", e.getMessage());
        }

        try {
            xmlFileAnonymizerService.preventDataFileCollisions(Arrays.asList(
                    createDataFile("a", "c"),
                    createDataFile("b", "c")
            ));
        } catch (RuntimeException e) {
            assertEquals("Configuration will write twice to the same file c.", e.getMessage());
        }
    }


    public void testIntegrationTest() throws Exception {
        // Create testfile
        File tempInput = File.createTempFile("tempInput", ".xml");
        PrintWriter printWriter = new PrintWriter(tempInput);
        printWriter.write("<top><rec><field1>test1</field1><field2>notouch</field2><field3>test2</field3><field4>test3</field4><field5>transient</field5></rec>");
        printWriter.write("<rec><field6>test3</field6><field2>notouch</field2><field3>test2</field3><field4>test1</field4><field5>transient</field5></rec></top>");
        printWriter.close();

        String tempOutput = tempInput.getAbsoluteFile() + ".out.xml";

        List<Column> columns = Arrays.asList(
                new Column("rec/field1", "STRING", 50),
                new Column("rec/field3", "STRING", 50),
                new Column("rec/field4", "STRING", 50),
                new Column("rec/field5", "STRING", 50, true),
                new Column("rec/field6", "STRING", 50));

        DataFile dataFile = new DataFile();
        dataFile.setInFile(tempInput.getAbsolutePath());
        dataFile.setOutFile(tempOutput);
        dataFile.setReader(CsvFileReader.class.getCanonicalName());
        dataFile.setWriter(CsvFileWriter.class.getCanonicalName());
        dataFile.setColumns(columns);

        Configuration configuration = new Configuration();
        configuration.setFiles(Arrays.asList(dataFile));
        AnonymizerService anonymizerService = new AnonymizerService();
        anonymizerService.registerAnonymizers(configuration.getAnonymizerClasses());
        xmlFileAnonymizerService = new XMLFileAnonymizerService(configuration, anonymizerService);

        xmlFileAnonymizerService.anonymize();

        File outPutFile = new File(tempOutput);
        assertTrue(outPutFile.exists());

        SAXBuilder jdomBuilder = new SAXBuilder();
        Document outputDocument = jdomBuilder.build(outPutFile.getAbsoluteFile());
        Element top = outputDocument.getRootElement();

        XPathFactory xFactory = XPathFactory.instance();
        XPathExpression<Element> expr = xFactory.compile("//top/rec", Filters.element());
        List<Element> hits = expr.evaluate(top);

        Element outputRecord1 = hits.get(0);
        Element outputRecord2 = hits.get(1);

        assertEquals(outputRecord1.getChild("field1").getValue(), outputRecord2.getChild("field4").getValue());
        assertEquals(outputRecord1.getChild("field2").getValue(), outputRecord2.getChild("field2").getValue());
        assertEquals(outputRecord1.getChild("field3").getValue(), outputRecord2.getChild("field3").getValue());
        assertEquals(outputRecord1.getChild("field4").getValue(), outputRecord2.getChild("field6").getValue());

        // The fifth column is transient, so anonymization will not be consistent,
        // as the value is not stored in the cache.
        assertNotEquals(outputRecord1.getChild("field5").getValue(), outputRecord2.getChild("field5").getValue());
        assertNotEquals("transient", outputRecord2.getChild("field5").getValue());

        assertEquals("notouch", outputRecord1.getChild("field2").getValue());
        assertEquals("notouch", outputRecord2.getChild("field2").getValue());

        assertEquals(3, anonymizerService.getSynonymCache().size());
    }

    private DataFile createDataFile(String infile, String outfile) {
        DataFile dataFile = new DataFile();
        dataFile.setInFile(infile);
        dataFile.setOutFile(outfile);
        return dataFile;
    }


}
