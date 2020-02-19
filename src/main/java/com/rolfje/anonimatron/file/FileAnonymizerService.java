package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.DataFile;
import com.rolfje.anonimatron.progress.Progress;
import com.rolfje.anonimatron.progress.ProgressPrinter;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Reads rows from a file and returns anonymized rows.
 */
public class FileAnonymizerService {
    private Logger LOG = Logger.getLogger(FileAnonymizerService.class);

    private Configuration config;
    private AnonymizerService anonymizerService;
    private Progress progress;


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
        System.out.println("Files to process: " + files.size());

        List<FileFilter> fileFilters = getFileFilters();

        long totalBytes = 0;
        for (DataFile file : files) {
            totalBytes += new File(file.getInFile()).length();
        }

        progress = new Progress();
        progress.setTotalitemstodo(totalBytes);

        ProgressPrinter printer = new ProgressPrinter(progress);
        printer.setPrintIntervalMillis(1000);

// 		Enable this when printing is better tested.
//		printer.start();

        for (DataFile file : files) {
            File infile = new File(file.getInFile());

            boolean process = true;
            for (FileFilter fileFilter : fileFilters) {
                if (!fileFilter.accept(infile)) {
                    // Skip file
                    process = false;
                    progress.incItemsCompleted(infile.length());
                    continue;
                }
            }

            if (!process || new File(file.getOutFile()).exists()) {
                System.out.println("Skipping " + file.getInFile());
                progress.incItemsCompleted(infile.length());
                continue;
            }

            System.out.println("Anonymizing from " + file.getInFile());
            System.out.println("              to " + file.getOutFile());

            RecordReader reader = createReader(file);
            RecordWriter writer = createWriter(file);

            Map<String, Column> columns = toMap(file.getColumns());

            anonymize(
                    reader,
                    writer,
                    columns
            );

            reader.close();
            writer.close();
            progress.incItemsCompleted(infile.length());
        }
        printer.stop();

        System.out.println("\nAnonymization process completed.\n");
    }

    private List<FileFilter> getFileFilters() throws Exception {
        List<FileFilter> fileFilters = new ArrayList<FileFilter>();
        List<String> fileFilterStrings = config.getFileFilters();
        if (fileFilterStrings != null) {
            for (String fileFilterString : fileFilterStrings) {
                fileFilters.add(createFileFilter(fileFilterString));
            }
        }
        return fileFilters;
    }

    private List<DataFile> expandDirectories(List<DataFile> files) {
        ArrayList<DataFile> allFiles = new ArrayList<DataFile>();

        if (files == null || files.isEmpty()) {
            return allFiles;
        }

        for (DataFile dataFile : files) {

            // Get all input files
            List<File> inFiles = getInputFiles(dataFile);

            // Check that we don't overwrite output files
            for (File inFile : inFiles) {

                File outFile = new File(dataFile.getOutFile());
                if (outFile.exists() && outFile.isDirectory()) {
                    outFile = new File(outFile, inFile.getName());
                }

                if (outFile.exists()) {
                    throw new RuntimeException("Output file exists: " + outFile.getAbsolutePath());
                }

                DataFile newDataFile = new DataFile();
                newDataFile.setColumns(dataFile.getColumns());
                newDataFile.setReader(dataFile.getReader());
                newDataFile.setWriter(dataFile.getWriter());
                newDataFile.setInFile(inFile.getAbsolutePath());
                newDataFile.setOutFile(outFile.getAbsolutePath());
                newDataFile.setDiscriminators(dataFile.getDiscriminators());
                allFiles.add(newDataFile);
            }
        }

        preventDataFileCollisions(allFiles);
        return allFiles;
    }

    void preventDataFileCollisions(List<DataFile> allFiles) {
        HashSet<String> inFiles = new HashSet<>();
        for (DataFile dataFile : allFiles) {
            inFiles.add(dataFile.getInFile());
        }

        HashSet<String> outFiles = new HashSet<String>();
        for (DataFile dataFile : allFiles) {
            if (dataFile.getOutFile().equals(dataFile.getInFile())) {
                throw new RuntimeException("File used as both input and output: " + dataFile.getInFile() + ".");
            }

            if (outFiles.contains(dataFile.getOutFile())) {
                throw new RuntimeException("Configuration will write twice to the same file " + dataFile.getOutFile() + ".");
            }

            if (inFiles.contains(dataFile.getOutFile())) {
                throw new RuntimeException("Configuration will overwrite input file " + dataFile.getOutFile() + ".");
            }

            outFiles.add(dataFile.getOutFile());
        }
    }

    List<File> getInputFiles(DataFile dataFile) {
        List<File> inFiles = new ArrayList();
        File inFile = new File(dataFile.getInFile());

        if (inFile.exists() && inFile.isDirectory()) {
            File[] inputFiles = inFile.listFiles();

            for (int i = 0; i < inputFiles.length; i++) {
                File inputFile = inputFiles[i];
                if (inputFile.isFile()) {
                    inFiles.add(inputFile);
                }
            }

        } else if (inFile.exists() && inFile.isFile()) {
            inFiles.add(inFile);
        } else {
            throw new RuntimeException("Input file does not exist: " + inFile.getAbsolutePath());
        }
        return inFiles;
    }

    void anonymize(RecordReader reader, RecordWriter writer, Map<String, Column> columns) throws Exception {
        while (reader.hasRecords()) {
            Record read = reader.read();

            if (read != null) {
                Record anonymized = anonymize(read, columns);
                writer.write(anonymized);
            }
        }
    }

    Record anonymize(Record record, Map<String, Column> columns) {
        Object[] values = new Object[record.getValues().length];
        for (int i = 0; i < record.getNames().length; i++) {
            String name = record.getNames()[i];
            Object value = record.getValues()[i];

            if (columns.containsKey(name)) {
                Column column = columns.get(name);
                Synonym synonym = anonymizerService.anonymize(column, value);
                values[i] = synonym.getTo();
            } else {
                values[i] = value;
            }
        }

        Record outputRecord = new Record(record.getNames(), values);

        if (LOG.isTraceEnabled()) {
            LOG.trace(record);
            LOG.trace(outputRecord);
        }
        return outputRecord;
    }

    private Map<String, Column> toMap(List<Column> columns) {
        HashMap<String, Column> map = new HashMap<String, Column>();

        if (columns == null || columns.isEmpty()) {
            return map;
        }

        for (Column column : columns) {
            map.put(column.getName(), column);
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

    private FileFilter createFileFilter(String fileFilterClass) throws Exception {
        try {
            Class clazz = Class.forName(fileFilterClass);
            Constructor constructor = clazz.getConstructor();
            return (FileFilter) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Problem creating file filter " + fileFilterClass + ".", e);
        }
    }

}
