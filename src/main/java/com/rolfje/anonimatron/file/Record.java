package com.rolfje.anonimatron.file;

public class Record {
	final String[] types;
	final Object[] values;

	public Record(String[] types, Object[] values) {
		if (types.length != values.length) {
			throw new IllegalArgumentException("Argument Arrays need to be the same size.");
		}
		this.types = types;
		this.values = values;
	}
}
