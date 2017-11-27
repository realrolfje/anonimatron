package com.rolfje.anonimatron.file;

import java.io.Closeable;

/**
 * Consumes records which were anonymized by {@link FileAnonymizerService}.
 */
public interface RecordWriter extends Closeable {
	void write(Record record);
}
