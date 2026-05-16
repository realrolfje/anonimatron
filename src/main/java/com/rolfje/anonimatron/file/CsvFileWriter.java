package com.rolfje.anonimatron.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CsvFileWriter implements RecordWriter, ParameterizedRecordWriter {
	public static final String DELIMITER_PARAMETER = "delimiter";

	private BufferedWriter writer;
	private File file;
	private String delimiter = ",";

	public CsvFileWriter(String fileName) throws IOException {
		this(new File(fileName));
	}

	public CsvFileWriter(File file) throws IOException {
		this(file, StandardCharsets.UTF_8);
	}

	public CsvFileWriter(String fileName, String encoding) throws IOException {
		this(new File(fileName), Charset.forName(encoding));
	}

	public CsvFileWriter(File file, String encoding) throws IOException {
		this(file, Charset.forName(encoding));
	}

	private CsvFileWriter(File file, Charset charset) throws IOException {
		this.file = file;
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
	}

	@Override
	public void write(Record record) {
		StringBuilder line = new StringBuilder();

		Object[] values = record.getValues();
		for (int i = 0; i < values.length; i++) {
			String value = values[i].toString();
			line.append(value);
			if (i < values.length - 1) {
				line.append(delimiter);
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

	@Override
	public void setParameters(Map<String, String> parameters) {
		if (parameters == null) {
			return;
		}

		String configuredDelimiter = parameters.get(DELIMITER_PARAMETER);
		if (configuredDelimiter != null) {
			delimiter = configuredDelimiter;
		}
	}
}
