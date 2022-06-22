package com.rolfje.anonimatron.file;

import junit.framework.TestCase;

import java.io.*;

public class CsvFileReaderTest extends TestCase {

	public void testHappy() throws IOException {
		File tempFile = File.createTempFile(CsvFileReaderTest.class.getSimpleName(), ".csv");
		Writer writer = new BufferedWriter(new FileWriter(tempFile));
		writer.write("\"Testfield1\";\"Testfield2\"");
		writer.close();

		CsvFileReader csvFileReader = new CsvFileReader(tempFile);
		assertTrue(csvFileReader.hasRecords());
		Record read = csvFileReader.read();

		assertEquals("Testfield1", read.getValues()[0]);
		assertEquals("Testfield2", read.getValues()[1]);
	}

	public void testNoQuotes() throws IOException {
		File tempFile = File.createTempFile(CsvFileReaderTest.class.getSimpleName(), ".csv");
		Writer writer = new BufferedWriter(new FileWriter(tempFile));
		writer.write("Testfield1;Testfield2");
		writer.close();

		CsvFileReader csvFileReader = new CsvFileReader(tempFile);
		assertTrue(csvFileReader.hasRecords());
		Record read = csvFileReader.read();

		assertEquals("Testfield1", read.getValues()[0]);
		assertEquals("Testfield2", read.getValues()[1]);
	}
}