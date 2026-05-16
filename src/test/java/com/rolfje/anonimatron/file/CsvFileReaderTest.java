package com.rolfje.anonimatron.file;

import junit.framework.TestCase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

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

	public void testReadsUtf8EncodedCsvByDefault() throws IOException {
		File tempFile = File.createTempFile(CsvFileReaderTest.class.getSimpleName(), ".csv");
		Files.write(tempFile.toPath(), "\"André\";\"München\"".getBytes(StandardCharsets.UTF_8));

		CsvFileReader csvFileReader = new CsvFileReader(tempFile);
		assertTrue(csvFileReader.hasRecords());
		Record read = csvFileReader.read();

		assertEquals("André", read.getValues()[0]);
		assertEquals("München", read.getValues()[1]);
	}

	public void testReadsIso88591EncodedCsvWhenConfigured() throws IOException {
		File tempFile = File.createTempFile(CsvFileReaderTest.class.getSimpleName(), ".csv");
		// Reproduces https://github.com/realrolfje/anonimatron/issues/235.
		Files.write(tempFile.toPath(), "\"André\";\"München\"".getBytes(StandardCharsets.ISO_8859_1));

		CsvFileReader csvFileReader = new CsvFileReader(tempFile, "ISO-8859-1");
		assertTrue(csvFileReader.hasRecords());
		Record read = csvFileReader.read();

		assertEquals("André", read.getValues()[0]);
		assertEquals("München", read.getValues()[1]);
	}

	public void testReadsConfiguredDelimiter() throws IOException {
		File tempFile = File.createTempFile(CsvFileReaderTest.class.getSimpleName(), ".csv");
		Writer writer = new BufferedWriter(new FileWriter(tempFile));
		writer.write("Testfield1|Testfield2");
		writer.close();

		CsvFileReader csvFileReader = new CsvFileReader(tempFile);
		Map<String, String> parameters = new HashMap<>();
		parameters.put(CsvFileReader.DELIMITER_PARAMETER, "|");
		csvFileReader.setParameters(parameters);

		assertTrue(csvFileReader.hasRecords());
		Record read = csvFileReader.read();

		assertEquals("Testfield1", read.getValues()[0]);
		assertEquals("Testfield2", read.getValues()[1]);
	}
}
