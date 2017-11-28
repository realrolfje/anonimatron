package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.anonymizer.DutchBSNAnononymizer;
import com.rolfje.anonimatron.anonymizer.StringAnonymizer;
import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.DataFile;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
		Record record = new Record(new String[]{"bsn"}, new String[]{"ABC"});

		HashMap<String, Column> nameTypeMap = new HashMap<String, Column>();
		nameTypeMap.put("bsn", new Column("bsn", new DutchBSNAnononymizer().getType(), 50));

		Record anonymize = fileAnonymizerService.anonymize(record, nameTypeMap);

		assertEquals(record.getNames()[0], anonymize.getNames()[0]);
		assertFalse(record.getValues()[0].equals(anonymize.getValues()[0]));
	}

	public void testAnonymizeRecords() throws Exception {
		String[] names = new String[]{
				new DutchBSNAnononymizer().getType(),
				new StringAnonymizer().getType()
		};

		final List<Record> sourceRecords = new ArrayList<Record>();

		for (int i = 0; i < 10; i++) {
			Object[] values = new Object[]{
					"MyBSN",
					"some private text"
			};
			sourceRecords.add(new Record(names, values));
		}

		RecordReader recordReader = new RecordReader() {
			@Override
			public void close() throws IOException {
				// nothing
			}

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
			public void close() throws IOException {
				// nothing
			}

			@Override
			public void write(Record record) {
				targetRecords.add(record);
			}
		};

		HashMap<String, Column> nameTypeMap = new HashMap<String, Column>();
		nameTypeMap.put(names[0], new Column(names[0], names[0], 50));
		nameTypeMap.put(names[1], new Column(names[1], names[1], 50));

		fileAnonymizerService.anonymize(recordReader, recordWriter, nameTypeMap);
		assertEquals(sourceRecords.size(), targetRecords.size());

		for (int i = 0; i < sourceRecords.size(); i++) {
			Record source = sourceRecords.get(i);
			Record target = targetRecords.get(i);

			for (int j = 0; j < source.getNames().length; j++) {
				assertEquals(source.getNames()[j], target.getNames()[j]);
				assertFalse(source.getValues()[j].equals(target.getNames()[j]));
				assertNotNull(target.getNames()[j]);
			}
		}
	}

	public void testIntegrationTest() throws Exception {
		// Create testfile
		File tempInput = File.createTempFile("tempInput", ".csv");
		PrintWriter printWriter = new PrintWriter(tempInput);
		printWriter.write("test1,notouch,test2,test3\n");
		printWriter.write("test3,notouch,test2,test1\n");
		printWriter.close();

		String tempOutput = tempInput.getAbsoluteFile() + ".out.csv";

		List<Column> columns = Arrays.asList(
				new Column[]{
						new Column("1", "STRING", 50),
						new Column("3", "STRING", 50),
						new Column("4", "STRING", 50),
				});

		DataFile dataFile = new DataFile();
		dataFile.setInFile(tempInput.getAbsolutePath());
		dataFile.setOutFile(tempOutput);
		dataFile.setReader(CsvFileReader.class.getCanonicalName());
		dataFile.setWriter(CsvFileWriter.class.getCanonicalName());
		dataFile.setColumns(columns);

		Configuration configuration = new Configuration();
		configuration.setFiles(Arrays.asList(new DataFile[]{dataFile}));
		AnonymizerService anonymizerService = new AnonymizerService();
		anonymizerService.registerAnonymizers(configuration.getAnonymizerClasses());
		fileAnonymizerService = new FileAnonymizerService(configuration, anonymizerService);

		fileAnonymizerService.anonymize();

		File outPutFile = new File(tempOutput);
		assertTrue(outPutFile.exists());

		CsvFileReader csvFileReader = new CsvFileReader(outPutFile.getAbsoluteFile());
		Record outputRecord1 = csvFileReader.read();
		Record outputRecord2 = csvFileReader.read();
		assertFalse(csvFileReader.hasRecords());

		assertEquals(outputRecord1.getValues()[0], outputRecord2.getValues()[3]);
		assertEquals(outputRecord1.getValues()[1], outputRecord2.getValues()[1]);
		assertEquals(outputRecord1.getValues()[2], outputRecord2.getValues()[2]);
		assertEquals(outputRecord1.getValues()[3], outputRecord2.getValues()[0]);

		assertEquals("notouch", outputRecord1.getValues()[1]);
		assertEquals("notouch", outputRecord2.getValues()[1]);
	}
}