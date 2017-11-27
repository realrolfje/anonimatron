package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.DataFile;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

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


	public void printConfigurationInfo() {
		System.out.println("\nAnonymization process started\n");
		System.out.println("To do         : " + config.getFiles().size() + " files.\n");
	}


	public void anonymize() throws Exception {
		List<DataFile> files = expandDirectories(config.getFiles());


		for (DataFile file : files) {
			System.out.println("Anonymizing from " + file.getInFile());
			System.out.println("              to " + file.getOutFile());

			RecordReader reader = createReader(file);
			RecordWriter writer = createWriter(file);

			Map<String, String> columns = toMap(file.getColumns());

			anonymize(
					reader,
					writer,
					columns
			);

			reader.close();
			writer.close();
		}

		System.out.println("\nAnonymization process completed.\n");
	}

	private List<DataFile> expandDirectories(List<DataFile> files) {
		ArrayList<DataFile> allFiles = new ArrayList<DataFile>();

		for (DataFile file : files) {
			File inFile = new File(file.getInFile());
			File outFile = new File(file.getOutFile());

			if (inFile.exists() && inFile.isDirectory()) {
				if (outFile.exists() && outFile.isDirectory()) {
					// Input and Output are directories. Expand to single elements.
					File[] inputFiles = inFile.listFiles();
					for (int i = 0; i < inputFiles.length; i++) {
						File inputFile = inputFiles[i];
						File outputFile = new File(outFile.getAbsoluteFile() + File.separator + inputFile.getName());

						if (inputFile.isDirectory()) {
							continue;
						}

						DataFile dataFile = new DataFile();
						dataFile.setColumns(file.getColumns());
						dataFile.setReader(file.getReader());
						dataFile.setWriter(file.getWriter());
						dataFile.setInFile(inputFile.getAbsolutePath());
						dataFile.setOutFile(outputFile.getAbsolutePath());
						dataFile.setDiscriminators(file.getDiscriminators());
						allFiles.add(dataFile);
					}
				}
				else if (outFile.exists() && outFile.isFile()) {
					throw new RuntimeException("Input " + inFile.getName() + " is a directory, but output " + outFile.getName() + " is a file. Check configuration.");
				}
			}
			else if (inFile.exists() && inFile.isFile() &&
					outFile.exists() && outFile.isDirectory()) {
				// Create output filename if it is a directory
				File outputFile = new File(outFile.getAbsoluteFile() + File.separator + inFile.getName());

				DataFile dataFile = new DataFile();
				dataFile.setColumns(file.getColumns());
				dataFile.setReader(file.getReader());
				dataFile.setWriter(file.getWriter());
				dataFile.setInFile(inFile.getAbsolutePath());
				dataFile.setOutFile(outputFile.getAbsolutePath());
				dataFile.setDiscriminators(file.getDiscriminators());
				allFiles.add(dataFile);
			}
			else {
				// No special treatment
				allFiles.add(file);
			}
		}

		HashSet<String> outFiles = new HashSet<String>();
		for (DataFile file : allFiles) {
			if (file.getOutFile().equals(file.getInFile())) {
				throw new RuntimeException("File used as both input and output: " + file.getInFile() + ".");
			}

			if (outFiles.contains(file.getOutFile())) {
				throw new RuntimeException("Configuration will write twice to the same file " + file.getOutFile() + ".");
			}

			outFiles.add(file.getOutFile());
		}

		return allFiles;
	}

	void anonymize(RecordReader reader, RecordWriter writer, Map<String, String> columns) throws Exception {
		while(reader.hasRecords()) {
			Record read = reader.read();
			Record anonymized = anonymize(read, columns);
			writer.write(anonymized);
		}
	}

	Record anonymize(Record record, Map<String, String> columns) {
		Object[] values = new Object[record.values.length];
		for (int i = 0; i < record.names.length; i++) {
			String name = record.names[i];
			Object value = record.values[i];

			if (columns.containsKey(name)) {
				String type = columns.get(name);
				Synonym synonym = anonymizerService.anonymize(type, value, Integer.MAX_VALUE);
				values[i] = synonym.getTo();
			} else {
				values[i] = value;
			}
		}
		return new Record(record.names, values);
	}

	private Map<String, String> toMap(List<Column> columns) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (Column column : columns) {
			map.put(column.getName(), column.getType());
		}
		return map;
	}

	private RecordReader createReader(DataFile file) throws Exception {
		try {
			Class clazz = Class.forName(file.getReader());
			Constructor constructor = clazz.getConstructor(String.class);
			return (RecordReader) constructor.newInstance(file.getInFile());
		} catch (Exception e) {
			throw new RuntimeException("Problem creating reader " + file.getReader() + " for input file " + file.getInFile() + ".", e);
		}
	}

	private RecordWriter createWriter(DataFile file) throws Exception {
		try {
			Class clazz = Class.forName(file.getWriter());
			Constructor constructor = clazz.getConstructor(String.class);
			return (RecordWriter) constructor.newInstance(file.getOutFile());
		} catch (Exception e) {
			throw new RuntimeException("Problem creating writer " + file.getWriter() + " for output file " + file.getOutFile() + ".", e);
		}
	}
}
