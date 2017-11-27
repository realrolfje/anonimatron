package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.Anonymizer;
import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.DataFile;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Reads rows from a file and returns anonymized rows.
 */
public class FileAnonymizerService {
	private Logger LOG = Logger.getLogger(FileAnonymizerService.class);

	private Configuration config;
	private AnonymizerService anonymizerService;


	public FileAnonymizerService(Configuration config, AnonymizerService anonymizerService) {
		this.config = config;
		this.anonymizerService = anonymizerService;
	}


	public void anonymize() throws Exception {
		List<DataFile> files = config.getFiles();
		for (DataFile file : files) {
			Class fileReaderClass = Class.forName(file.getReader());
			Constructor constructor = fileReaderClass.getConstructor(String.class);
			RecordReader recordReader = (RecordReader) constructor.newInstance(file.getName());

			anonymize(recordReader, new RecordWriter() {
				@Override
				public void write(Record record) {
					System.out.println(record.toString());
				}
			});
		}
	}

	public void anonymize(RecordReader reader, RecordWriter writer) throws Exception {
		while(reader.hasRecords()) {
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
