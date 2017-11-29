package com.rolfje.anonimatron.file;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CsvFileReader implements RecordReader, Closeable {

	private BufferedReader reader;
	private File file;

	public CsvFileReader(String fileName) throws IOException {
		this(new File(fileName));
	}

	public CsvFileReader(File file) throws IOException {
		this.file = file;
		try {
			reader = new BufferedReader(new FileReader(file));
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
		StringTokenizer stringTokenizer = new StringTokenizer(s, ",");

		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> strings = new ArrayList<String>();
		int i = 1;
		while (stringTokenizer.hasMoreTokens()) {
			names.add(String.valueOf(i));
			strings.add(stringTokenizer.nextToken());
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
}
