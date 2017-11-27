package com.rolfje.anonimatron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import com.rolfje.anonimatron.anonymizer.Anonymizer;
import com.rolfje.anonimatron.file.CsvFileWriter;
import com.rolfje.anonimatron.file.Record;
import junit.framework.TestCase;

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
		csvFileWriter.write(new Record(
				new String[]{"ignored", "value1"},
				new String[]{"ignored", "value2"}
		));
		csvFileWriter.close();

		File outFile = File.createTempFile(this.getClass().getSimpleName(), ".output.csv");

		File configFile = File.createTempFile(this.getClass().getSimpleName(), ".config.xml)");
		PrintWriter printWriter = new PrintWriter(configFile);
		printWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		printWriter.write("<configuration>\n");
		printWriter.write("<file " +
				"inFile=\""+inFile.getAbsolutePath()+"\" reader=\"com.rolfje.anonimatron.file.CsvFileReader\" " +
				"outFile=\""+outFile.getAbsolutePath()+"\" writer=\"com.rolfje.anonimatron.file.CsvFileWriter\" " +
				">\n");
		printWriter.write("<column name=\"1\" type=\"ROMAN_NAME\"/>\n");
		printWriter.write("</file>\n");
		printWriter.write("</configuration>\n");
		printWriter.close();

		String[] arguments = new String[]{"-config", configFile.getAbsolutePath()};
		Anonimatron.main(arguments);
	}
}
