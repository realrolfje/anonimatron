package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.log4j.Logger;

/**
 * Reads rows from a file and returns anonymized rows.
 */
public class FileAnonymizerService {
	private Logger LOG = Logger.getLogger(FileAnonymizerService.class);

	private Configuration config;
	private AnonymizerService anonymizerService;


	public FileAnonymizerService(Configuration config, AnonymizerService anonymizerService) {



		this.anonymizerService = anonymizerService;
	}

	public void anonymize(RecordReader reader, RecordWriter writer) {
		while (reader.hasRecords()) {
			writer.write(anonymize(reader.read()));
		}
	}

	Record anonymize(Record record) {
		Object[] values = new Object[record.values.length];
		for (int i = 0; i < record.types.length; i++) {
			String type = record.types[i];
			Object value = record.values[i];

			Synonym synonym = anonymizerService.anonymize(type, value, Integer.MAX_VALUE);
			values[i] = synonym.getTo();
		}
		return new Record(record.types, values);
	}
}
