package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.*;
import com.rolfje.anonimatron.configuration.Configuration;
import junit.framework.TestCase;

import java.util.*;

public class FileAnonymizerServiceTest extends TestCase {

	private FileAnonymizerService fileAnonymizerService;

	public void setUp() throws Exception {
		super.setUp();

		Configuration config = new Configuration();
		AnonymizerService anonymizerService = new AnonymizerService();
		anonymizerService.registerAnonymizers(config.getAnonymizerClasses());
		fileAnonymizerService = new FileAnonymizerService(config, anonymizerService);
	}

	public void testAnonymize() throws Exception {
		Record record = new Record(new String[]{new DutchBSNAnononymizer().getType()}, new String[]{"ABC"});
		Record anonymize = fileAnonymizerService.anonymize(record);

		assertEquals(record.types[0], anonymize.types[0]);
		assertFalse(record.values[0].equals(anonymize.values[0]));
	}

	public void testAnonymizeRecords() throws Exception {
		String[] types = new String[]{
				new DutchBSNAnononymizer().getType(),
				new StringAnonymizer().getType()
		};

		final List<Record> sourceRecords = new ArrayList<Record>();

		for (int i = 0; i < 10; i++) {
			Object[] values = new Object[]{
					"MyBSN",
					"some private text"
			};
			sourceRecords.add(new Record(types, values));
		}

		RecordReader recordReader = new RecordReader() {
			private int i = 0;


			@Override
			public boolean hasRecords() {
				return i < sourceRecords.size();
			}

			@Override
			public Record read() {
				Record record = sourceRecords.get(i);
				i = i + 1;
				return record;
			}
		};

		final List<Record> targetRecords = new ArrayList<Record>();
		RecordWriter recordWriter = new RecordWriter() {
			@Override
			public void write(Record record) {
				targetRecords.add(record);
			}
		};

		fileAnonymizerService.anonymize(recordReader, recordWriter);
		assertEquals(sourceRecords.size(), targetRecords.size());

		for (int i = 0; i < sourceRecords.size(); i++) {
			Record source = sourceRecords.get(i);
			Record target = targetRecords.get(i);

			for (int j = 0; j < source.types.length; j++) {
				assertEquals(source.types[j], target.types[j]);
				assertFalse(source.values[j].equals(target.types[j]));
				assertNotNull(target.types[j]);
			}
		}
	}
}