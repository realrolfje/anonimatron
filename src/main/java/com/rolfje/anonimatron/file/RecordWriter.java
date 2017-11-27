package com.rolfje.anonimatron.file;

/**
 * Consumes records which were anonymized by {@link FileAnonymizerService}.
 */
public interface RecordWriter {
	void write(Record record);
}
