package com.rolfje.anonimatron.file;

/**
 * Provides records to be anonymized the {@link FileAnonymizerService}.
 */
public interface RecordReader {
	boolean hasRecords();
	Record read();
}
