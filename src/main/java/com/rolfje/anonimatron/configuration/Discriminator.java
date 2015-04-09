package com.rolfje.anonimatron.configuration;

import java.util.List;

/**
 * Provides a way to apply anonymization of certain columns based on the value
 * of another column. Discriminators override the default column confgurations
 * in case of collision.
 * 
 * @author rolf
 */
public class Discriminator {
	String columnName;
	String value;
	List<Column> columns;

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
