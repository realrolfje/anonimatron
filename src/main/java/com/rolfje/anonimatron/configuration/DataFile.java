package com.rolfje.anonimatron.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFile {
	private String inFile;
	private String reader;

	private String outFile;
	private String writer;

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

	public List<Column> getColumns() {
		return columns;
	}

	public static Map<String, Column> getColumnsAsMap(List<Column> columns) {
		Map<String, Column> columnMap = new HashMap<String, Column>();
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
