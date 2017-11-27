package com.rolfje.anonimatron.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFile {
	private String name;
	private String reader;
	private List<Column> columns;
	private List<Discriminator> discriminators;

	private long numberOfRecords;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public String getReader() {
		return reader;
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
