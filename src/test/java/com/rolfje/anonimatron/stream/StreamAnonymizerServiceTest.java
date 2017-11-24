package com.rolfje.anonimatron.stream;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.anonymizer.DateAnonymizer;
import com.rolfje.anonimatron.anonymizer.DutchBSNAnononymizer;
import com.rolfje.anonimatron.anonymizer.UUIDAnonymizer;
import com.rolfje.anonimatron.configuration.Configuration;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StreamAnonymizerServiceTest extends TestCase {

	private StreamAnonymizerService streamAnonymizerService;

	public void setUp() throws Exception {
		super.setUp();

		Configuration configuration = new Configuration();
		AnonymizerService anonymizerService = new AnonymizerService();
		streamAnonymizerService = new StreamAnonymizerService(configuration, anonymizerService);
	}

	public void testAnonymize() throws Exception {
		Record record = new Record(new String[]{new DutchBSNAnononymizer().getType()}, new String[]{"ABC"});
		Record anonymize = streamAnonymizerService.anonymize(record);

		assertEquals(record.types[0], anonymize.types[0]);
		assertFalse(record.values[0].equals(anonymize.values[0]));
	}

	public void testAnonymizeRecords() throws Exception {
		String[] types = new String[]{
				new DutchBSNAnononymizer().getType(),
				new DateAnonymizer().getType(),
				new UUIDAnonymizer().getType()
		};

		final List<Record> sourceRecords = new ArrayList<Record>();

		for (int i = 0; i < 10; i++) {
			Object[] values = new Object[]{
					"MyBSN",
					new Date(),
					UUID.randomUUID()
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

		streamAnonymizerService.anonymize(recordReader, recordWriter);
		assertEquals(sourceRecords.size(), targetRecords.size());

		for (int i = 0; i < sourceRecords.size(); i++) {
			Record source = sourceRecords.get(i);
			Record target = targetRecords.get(i);

			for (int j = 0; j < source.types.length; j++) {
				assertEquals(source.types[i], target.types[i]);
				assertFalse(source.values[i].equals(target.types[i]));
				assertNotNull(target.types[i]);
			}
		}
	}
}