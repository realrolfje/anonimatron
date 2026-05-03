package com.rolfje.anonimatron.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFile {
	public static final String DEFAULT_ENCODING = "UTF-8";

	private String inFile;
	private String reader;
	private String encoding = DEFAULT_ENCODING;

	private String outFile;
	private String writer;

	private Map<String, String> readerParameters;
	private Map<String, String> writerParameters;

	private List<Column> columns;

	private List<Discriminator> discriminators;
	private long numberOfRecords;

	public String getInFile() {
		return inFile;
	}

	public void setInFile(String inFile) {
		this.inFile = inFile;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public String getReader() {
		return reader;
	}

	public String getEncoding() {
		return encoding == null || encoding.trim().isEmpty() ? DEFAULT_ENCODING : encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public Map<String, String> getReaderParameters() {
		return readerParameters;
	}

	public void setReaderParameters(Map<String, String> readerParameters) {
		this.readerParameters = readerParameters;
	}

	public Map<String, String> getWriterParameters() {
		return writerParameters;
	}

	public void setWriterParameters(Map<String, String> writerParameters) {
		this.writerParameters = writerParameters;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public static Map<String, Column> getColumnsAsMap(List<Column> columns) {
		Map<String, Column> columnMap = new HashMap<>();
		for (Column column : columns) {
			columnMap.put(column.getName(), column);
		}
		return columnMap;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Discriminator> getDiscriminators() {
		return discriminators;
	}

	public void setDiscriminators(List<Discriminator> discriminators) {
		this.discriminators = discriminators;
	}

	public long getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

}
