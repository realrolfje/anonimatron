package com.rolfje.anonimatron;

import com.rolfje.anonimatron.anonymizer.Hasher;
import com.rolfje.anonimatron.anonymizer.SynonymCache;
import com.rolfje.anonimatron.file.AcceptAllFilter;
import com.rolfje.anonimatron.file.CsvFileReader;
import com.rolfje.anonimatron.file.CsvFileWriter;
import com.rolfje.anonimatron.file.Record;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Unit test for simple App.
 */
public class AnonimatronTest extends TestCase {

    public void testVersion() throws Exception {
        File pom = new File("pom.xml");
        assertTrue("Could not find the project pom file.", pom.exists());

        String versionString = "<version>" + Anonimatron.VERSION + "</version>";

        try (BufferedReader reader = new BufferedReader(new FileReader(pom))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.contains(versionString)) {
                    // Version is ok, return
                    return;
                }

                if (line.contains("<dependencies>")) {
                    fail("Incorrect version, pom.xml does not match version info in Anonimatron.VERSION.");
                }
            }
        }
        fail("Incorrect version, pom.xml does not match version info in Anonimatron.VERSION.");
    }


    public void testIntegrationFileReader() throws Exception {
        // Create fake input file
        File inFile = File.createTempFile(this.getClass().getSimpleName(), ".input.csv");
        CsvFileWriter csvFileWriter = new CsvFileWriter(inFile);
        Record inputRecords = new Record(
                new String[]{"colname1", "colname2"},
                new String[]{"value1", "value2"}
        );
        csvFileWriter.write(inputRecords);
        csvFileWriter.close();

        File outFile = File.createTempFile(this.getClass().getSimpleName(), ".output.csv");
        assertTrue("Could not delete " + outFile, outFile.delete());

        File synonymFile = File.createTempFile(this.getClass().getSimpleName(), "synonyms.xml");
        assertTrue("Could not delete " + synonymFile, synonymFile.delete());

        File configFile = File.createTempFile(this.getClass().getSimpleName(), ".config.xml)");
        PrintWriter printWriter = new PrintWriter(configFile);
        printWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        printWriter.write("<configuration salt=\"testsalt\">\n");
        printWriter.write("<filefilterclass>com.rolfje.anonimatron.file.AcceptAllFilter</filefilterclass>");
        printWriter.write("<file " +
                "inFile=\"" + inFile.getAbsolutePath() + "\" reader=\"com.rolfje.anonimatron.file.CsvFileReader\" " +
                "outFile=\"" + outFile.getAbsolutePath() + "\" writer=\"com.rolfje.anonimatron.file.CsvFileWriter\" " +
                ">\n");
        printWriter.write("<column name=\"1\" type=\"ROMAN_NAME\" size=\"50\"/>\n");
        printWriter.write("</file>\n");
        printWriter.write("</configuration>\n");
        printWriter.close();

        String[] arguments = new String[]{
                "-config", configFile.getAbsolutePath(),
                "-synonyms", synonymFile.getAbsolutePath()
        };

        Anonimatron.main(arguments);

        assertEquals(1, AcceptAllFilter.getAcceptCount());

        // Check that original data is not present in the synonym file.
        SynonymCache synonymCache = SynonymCache.fromFile(synonymFile);
        Object[] inputValues = inputRecords.getValues();
        for (Object inputValue : inputValues) {
            assertNull(synonymCache.get("ROMAN_NAME", inputValue));
        }

        // Check that we can find the hashed first column value
        synonymCache.setHasher(new Hasher("testsalt"));
        assertNotNull("Hashed value not found.", synonymCache.get("ROMAN_NAME", inputValues[0]));
        assertNull("Hashed value found.", synonymCache.get("ROMAN_NAME", inputValues[1]));
    }

    public void testProcessesTablesAndFilesWhenBothConfigured() throws Exception {
        String jdbcurl = "jdbc:hsqldb:mem:" + getClass().getSimpleName() + System.nanoTime();
        Connection connection = DriverManager.getConnection(jdbcurl, "SA", "");
        connection.setAutoCommit(true);

        File inFile = File.createTempFile(this.getClass().getSimpleName(), ".input.csv");
        File outFile = File.createTempFile(this.getClass().getSimpleName(), ".output.csv");
        File synonymFile = File.createTempFile(this.getClass().getSimpleName(), "synonyms.xml");
        File configFile = File.createTempFile(this.getClass().getSimpleName(), ".config.xml)");

        try {
            executeSql(connection, "create table MIXEDDATA (SECRET VARCHAR(50), ID IDENTITY)");
            executeSql(connection, "insert into MIXEDDATA (SECRET) values ('shared')");

            try (PrintWriter printWriter = new PrintWriter(inFile)) {
                printWriter.write("shared,untouched\n");
            }

            assertTrue("Could not delete " + outFile, outFile.delete());
            assertTrue("Could not delete " + synonymFile, synonymFile.delete());

            try (PrintWriter printWriter = new PrintWriter(configFile)) {
                printWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                printWriter.write("<configuration jdbcurl=\"" + jdbcurl + "\" userid=\"SA\" password=\"\">\n");
                printWriter.write("  <table name=\"MIXEDDATA\">\n");
                printWriter.write("    <column name=\"SECRET\" type=\"STRING\" />\n");
                printWriter.write("  </table>\n");
                printWriter.write("  <file " +
                        "inFile=\"" + inFile.getAbsolutePath() + "\" reader=\"com.rolfje.anonimatron.file.CsvFileReader\" " +
                        "outFile=\"" + outFile.getAbsolutePath() + "\" writer=\"com.rolfje.anonimatron.file.CsvFileWriter\" " +
                        ">\n");
                printWriter.write("    <column name=\"1\" type=\"STRING\" />\n");
                printWriter.write("  </file>\n");
                printWriter.write("</configuration>\n");
            }

            Anonimatron.main(new String[]{
                    "-config", configFile.getAbsolutePath(),
                    "-synonyms", synonymFile.getAbsolutePath()
            });

            String databaseValue = selectSecret(connection);
            CsvFileReader csvFileReader = new CsvFileReader(outFile);
            Record outputRecord = csvFileReader.read();
            csvFileReader.close();

            assertFalse("Database value should be anonymized.", "shared".equals(databaseValue));
            assertEquals("File anonymization should reuse the table synonym.", databaseValue, outputRecord.getValues()[0]);
            assertEquals("untouched", outputRecord.getValues()[1]);
        } finally {
            executeSql(connection, "DROP SCHEMA PUBLIC CASCADE");
            connection.close();
        }
    }

    private void executeSql(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private String selectSecret(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("select SECRET from MIXEDDATA");
            try (ResultSet resultSet = statement.getResultSet()) {
                assertTrue(resultSet.next());
                return resultSet.getString(1);
            }
        }
    }

    public void testDemoConfiguration() throws Exception {
        Anonimatron.main(new String[]{"-configexample"});

    }
}
