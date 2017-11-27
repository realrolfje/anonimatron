package com.rolfje.anonimatron.file;

import java.io.Closeable;

/**
 * Provides records to be anonymized the {@link FileAnonymizerService}.
 */
public interface RecordReader extends Closeable{
	boolean hasRecords();
	Record read();
}
