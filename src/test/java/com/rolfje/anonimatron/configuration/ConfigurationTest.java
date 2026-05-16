package com.rolfje.anonimatron.configuration;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;

import junit.framework.TestCase;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;

/**
 * Prints out an XML to see what the Castor mapping makes and expects.
 * 
 */
public class ConfigurationTest extends TestCase {

	/**
	 * Prints an example configuration
	 * 
	 * @throws Exception
	 */
	public void testPrintMappedConfig() throws Exception {
		String demoxml = Configuration.getDemoConfiguration();
		assertNotNull(demoxml);
		assertTrue(demoxml.length()>10);

		// For convenience and visual checking.
		System.out.println(demoxml);

		// See if all anonymizer types are represented in the demo xml
		AnonymizerService as = new AnonymizerService();
		Set<String> customtypes = as.getCustomAnonymizerTypes();
		for (String type : customtypes) {
			assertTrue("Demo xml does not contain "+type,demoxml.indexOf("type=\""+type+"\"")>0);
		}
		
		Set<String> defaulttypes = as.getDefaultAnonymizerTypes();
		for (String type : defaulttypes) {
			assertTrue("Demo xml does not contain "+type,demoxml.indexOf(type.toUpperCase().replace('.','_'))>0);
		}

		assertTrue(demoxml.indexOf("A_SHORTLIVED_COLUMN") > 0);
		assertTrue(demoxml.indexOf("shortlived") > 0);

		// See if we have File records in the configuration.
		assertTrue(demoxml.contains("<file inFile=\"mydatafile.in.csv\""));
		assertTrue(demoxml.contains("encoding=\"UTF-8\""));

	}

	public void testReadFileEncodingFromConfiguration() throws Exception {
		File configFile = File.createTempFile(ConfigurationTest.class.getSimpleName(), ".xml");
		PrintWriter printWriter = new PrintWriter(configFile, "UTF-8");
		printWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		printWriter.write("<configuration salt=\"testsalt\">\n");
		printWriter.write("  <file inFile=\"in.csv\" reader=\"com.rolfje.anonimatron.file.CsvFileReader\" ");
		printWriter.write("encoding=\"ISO-8859-1\" ");
		printWriter.write("outFile=\"out.csv\" writer=\"com.rolfje.anonimatron.file.CsvFileWriter\" />\n");
		printWriter.write("</configuration>\n");
		printWriter.close();

		Configuration configuration = Configuration.readFromFile(configFile.getAbsolutePath());

		assertEquals("ISO-8859-1", configuration.getFiles().get(0).getEncoding());
	}

	public void testReadReaderAndWriterParametersFromConfiguration() throws Exception {
		File configFile = File.createTempFile(ConfigurationTest.class.getSimpleName(), ".xml");
		PrintWriter printWriter = new PrintWriter(configFile, "UTF-8");
		printWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		printWriter.write("<configuration salt=\"testsalt\">\n");
		printWriter.write("  <file inFile=\"in.csv\" reader=\"com.rolfje.anonimatron.file.CsvFileReader\" ");
		printWriter.write("outFile=\"out.csv\" writer=\"com.rolfje.anonimatron.file.CsvFileWriter\">\n");
		printWriter.write("    <readerParameter id=\"delimiter\">|</readerParameter>\n");
		printWriter.write("    <writerParameter id=\"delimiter\">;</writerParameter>\n");
		printWriter.write("  </file>\n");
		printWriter.write("</configuration>\n");
		printWriter.close();

		Configuration configuration = Configuration.readFromFile(configFile.getAbsolutePath());

		DataFile dataFile = configuration.getFiles().get(0);
		assertEquals("|", dataFile.getReaderParameters().get("delimiter"));
		assertEquals(";", dataFile.getWriterParameters().get("delimiter"));
	}

}
