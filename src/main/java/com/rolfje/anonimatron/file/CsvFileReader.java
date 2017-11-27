package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.StringAnonymizer;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CsvFileReader implements RecordReader, Closeable {

	private BufferedReader reader;

	public CsvFileReader(String fileName) throws IOException {
		this(new File(fileName));
	}

	public CsvFileReader(File file) throws IOException {
		reader = new BufferedReader(new FileReader(file));
	}

	@Override
	public boolean hasRecords() {
		try {
			return reader.ready();
		} catch (IOException e) {
			throw new RuntimeException("Problem while reading file.", e);
		}
	}

	@Override
	public Record read() {
		String s = null;
		try {
			s = reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException("Problem reading file.", e);
		}

		// Super simple implementation, not taking quotes into account.
		// The CSVReader library is no longer supporting Java 1.6, we need
		// to figure out if we want to switch to a newer Java.
		StringTokenizer stringTokenizer = new StringTokenizer(s, ",");

		ArrayList<String> types = new ArrayList<String>();
		ArrayList<String> strings = new ArrayList<String>();
		while (stringTokenizer.hasMoreTokens()) {
			types.add(new StringAnonymizer().getType());
			strings.add(stringTokenizer.nextToken());
		}

		return new Record(
				types.toArray(new String[]{}),
				strings.toArray()
		);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
