package com.rolfje.anonimatron.stream;

/**
 * Consumes records which were anonymized by {@link StreamAnonymizerService}.
 */
public interface RecordWriter {
	void write(Record record);
}
