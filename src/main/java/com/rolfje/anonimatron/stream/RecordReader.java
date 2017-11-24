package com.rolfje.anonimatron.stream;

/**
 * Provides records to be anonymized the {@link StreamAnonymizerService}.
 */
public interface RecordReader {
	boolean hasRecords();
	Record read();
}
