package com.rolfje.anonimatron.file;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CsvFileWriterTest extends TestCase {
	public void testWrite() throws Exception {
		File tempFile = File.createTempFile(CsvFileWriter.class.getSimpleName(), ".csv");
		tempFile.delete();

		CsvFileWriter csvFileWriter = new CsvFileWriter(tempFile);
		csvFileWriter.write(new Record(
				new String[]{"name1", "name2"},
				new String[]{"value1", "value2"}
		));
		csvFileWriter.close();

		BufferedReader bufferedReader = new BufferedReader(new FileReader(tempFile));
		String line = bufferedReader.readLine();
		assertEquals("value1,value2", line);
	}
}