package com.rolfje.anonimatron.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvFileWriter implements RecordWriter {

	private BufferedWriter writer;
	private File file;

	public CsvFileWriter(String fileName) throws IOException {
		this(new File(fileName));
	}

	public CsvFileWriter(File file) throws IOException {
		this.file = file;
		writer = new BufferedWriter(new FileWriter(file));
	}

	@Override
	public void write(Record record) {
		StringBuilder line = new StringBuilder();

		for (int i = 0; i < record.getValues().length; i++) {
			String value = record.getValues()[i].toString();
			line.append(value);
			if (i < record.getValues().length - 1) {
				line.append(",");
			}
		}

		try {
			writer.write(line.toString() + "\n");
		} catch (IOException e) {
			throw new RuntimeException("Problem writing file " + file.getAbsolutePath() + ".", e);
		}

	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}
