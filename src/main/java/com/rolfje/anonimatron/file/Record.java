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

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[Record: ");
		if (types != null) {
			for (int i = 0; i < types.length; i++) {
				stringBuilder.append(types[i]);
				stringBuilder.append(":");
				stringBuilder.append("'" + values + "' ");
			}
		}

		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
