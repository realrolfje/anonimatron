package com.rolfje.anonimatron;

import com.rolfje.anonimatron.anonymizer.Hasher;
import com.rolfje.anonimatron.anonymizer.SynonymCache;
import com.rolfje.anonimatron.file.AcceptAllFilter;
import com.rolfje.anonimatron.file.CsvFileWriter;
import com.rolfje.anonimatron.file.Record;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * Unit test for simple App.
 */
public class AnonimatronTest extends TestCase {

	public void testVersion() throws Exception {
		File pom = new File("pom.xml");
		assertTrue("Could not find the project pom file.",pom.exists());

		String versionString = "<version>"+Anonimatron.VERSION+"</version>";
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(pom));
			while(reader.ready()){
				String line = reader.readLine();
				if (line.contains(versionString)){
					// Version is ok, return
					return;
				}
				
				if (line.contains("<dependencies>")){
					fail("Incorrect version, pom.xml does not match version info in Anonimatron.VERSION.");
				}
			}
		} finally  {
			reader.close();
		}
		fail("Incorrect version, pom.xml does not match version info in Anonimatron.VERSION.");
	}


	public void testIntegrationFileReader() throws Exception {
		// Create fake input file
		File inFile = File.createTempFile(this.getClass().getSimpleName(), ".input.csv");
		CsvFileWriter csvFileWriter = new CsvFileWriter(inFile);
		Record inputRecords = new Record(
				new String[]{"colname1", "colname2"},
				new String[]{"value1", "value2"}
		);
		csvFileWriter.write(inputRecords);
		csvFileWriter.close();

		File outFile = File.createTempFile(this.getClass().getSimpleName(), ".output.csv");
		outFile.delete();

		File synonymFile = File.createTempFile(this.getClass().getSimpleName(), "synonyms.xml");
		synonymFile.delete();

		File configFile = File.createTempFile(this.getClass().getSimpleName(), ".config.xml)");
		PrintWriter printWriter = new PrintWriter(configFile);
		printWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		printWriter.write("<configuration salt=\"testsalt\">\n");
		printWriter.write("<filefilterclass>com.rolfje.anonimatron.file.AcceptAllFilter</filefilterclass>");
		printWriter.write("<file " +
				"inFile=\"" + inFile.getAbsolutePath() + "\" reader=\"com.rolfje.anonimatron.file.CsvFileReader\" " +
				"outFile=\"" + outFile.getAbsolutePath() + "\" writer=\"com.rolfje.anonimatron.file.CsvFileWriter\" " +
				">\n");
		printWriter.write("<column name=\"1\" type=\"ROMAN_NAME\" size=\"50\"/>\n");
		printWriter.write("</file>\n");
		printWriter.write("</configuration>\n");
		printWriter.close();

		String[] arguments = new String[]{
				"-config", configFile.getAbsolutePath(),
				"-synonyms", synonymFile.getAbsolutePath()
		};

		Anonimatron.main(arguments);

		assertEquals(1, AcceptAllFilter.getAcceptCount());

		// Check that original data is not present in the synonym file.
		SynonymCache synonymCache = SynonymCache.fromFile(synonymFile);
		Object[] inputValues = inputRecords.getValues();
		for (int i = 0; i < inputValues.length; i++) {
			assertNull(synonymCache.get("ROMAN_NAME", inputValues[i]));
		}

		// Check that we can find the hashed first column value
		synonymCache.setHasher(new Hasher("testsalt"));
		assertNotNull("Hashed value not found.", synonymCache.get("ROMAN_NAME", inputValues[0]));
		assertNull("Hashed value found.", synonymCache.get("ROMAN_NAME", inputValues[1]));
	}
}
