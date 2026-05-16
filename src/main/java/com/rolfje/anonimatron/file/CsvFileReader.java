package com.rolfje.anonimatron.file;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

public class CsvFileReader implements RecordReader, Closeable, ParameterizedRecordReader {
	public static final String DELIMITER_PARAMETER = "delimiter";

	private BufferedReader reader;
	private File file;
	private String delimiters = ",;\t";

	public CsvFileReader(String fileName) throws IOException {
		this(new File(fileName));
	}

	public CsvFileReader(File file) throws IOException {
		this(file, StandardCharsets.UTF_8);
	}

	public CsvFileReader(String fileName, String encoding) throws IOException {
		this(new File(fileName), Charset.forName(encoding));
	}

	public CsvFileReader(File file, String encoding) throws IOException {
		this(file, Charset.forName(encoding));
	}

	private CsvFileReader(File file, Charset charset) throws IOException {
		this.file = file;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problem while reading file " + file.getAbsolutePath() + ".", e);
		}
	}

	@Override
	public boolean hasRecords() {
		try {
			return reader.ready();
		} catch (IOException e) {
			throw new RuntimeException("Problem while reading file " + file.getAbsolutePath() + ".", e);
		}
	}

	@Override
	public Record read() {
		String s = null;
		try {
			s = reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException("Problem reading file " + file.getAbsolutePath() + ".", e);
		}

		// Super simple implementation, not taking quotes into account.
		// The CSVReader library is no longer supporting Java 1.6, we need
		// to figure out if we want to switch to a newer Java.
		StringTokenizer stringTokenizer = new StringTokenizer(s, delimiters);

		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> strings = new ArrayList<>();
		int i = 1;
		while (stringTokenizer.hasMoreTokens()) {
			names.add(String.valueOf(i));
			strings.add(stringTokenizer.nextToken().replaceAll("^\"|\"$", ""));
			i++;
		}

		return new Record(
				names.toArray(new String[]{}),
				strings.toArray()
		);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public void setParameters(Map<String, String> parameters) {
		if (parameters == null) {
			return;
		}

		String configuredDelimiter = parameters.get(DELIMITER_PARAMETER);
		if (configuredDelimiter != null) {
			delimiters = configuredDelimiter;
		}
	}
}
