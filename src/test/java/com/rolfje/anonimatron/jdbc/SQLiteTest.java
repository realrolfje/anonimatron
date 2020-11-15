package com.rolfje.anonimatron.jdbc;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.anonymizer.ToLowerAnonymizer;
import com.rolfje.anonimatron.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import static com.rolfje.anonimatron.jdbc.AbstractInMemoryHsqlDbTest.*;
import static junit.framework.TestCase.assertEquals;

public class SQLiteTest {
    private String TEST_DB_URL = "jdbc:sqlite::memory:";
    private Connection connection;

    //    {"jdbc:sqlite::memory:", "org.sqlite.JDBC"},
    //    {"jdbc:sqlite:[FILE]", "org.sqlite.JDBC"}


    @Before
    public void setUp() throws Exception {
        File file = File.createTempFile("SQLiteTest-", ".db");
        TEST_DB_URL = "jdbc:sqlite:" + file.getAbsolutePath();

        DriverManager.getDriver(TEST_DB_URL);
        connection = DriverManager.getConnection(TEST_DB_URL);
        connection.setAutoCommit(true);
    }

    @Test
    public void testCreateDatabase() throws Exception {
        // Create a table with some easy testdata
        executeSql(connection, "create table TABLE1 (COL1 VARCHAR(200), ID INT NOT NULL PRIMARY KEY)");
        PreparedStatement p = connection
                .prepareStatement("insert into TABLE1 (COL1, ID) values (?, ?)");
        for (int i = 0; i < 100; i++) {
            p.setString(1, "varcharstring-" + i);
            p.setInt(2, i);
            p.execute();
        }
        p.close();

        assertEquals(100, getIntResult(connection, "select count(*) from TABLE1"));

        Configuration config = new Configuration();
        config.setJdbcurl(TEST_DB_URL);
        config.setTables(new ArrayList<>());

        ArrayList<String> anonymizerclasses = new ArrayList<>();
        anonymizerclasses.add(ToLowerAnonymizer.class.getName());
        config.setAnonymizerClasses(anonymizerclasses);

        addToConfig(config, "TABLE1", "COL1", "TO_LOWER_CASE");
        anonymize(config, 100);
    }

    private AnonymizerService anonymize(Configuration config, int numberOfRecords) throws Exception {
        // Anonymize the data.
        AnonymizerService anonymizerService = new AnonymizerService();
        anonymizerService.registerAnonymizers(config.getAnonymizerClasses());
        JdbcAnonymizerService serv = new JdbcAnonymizerService(config, anonymizerService);
        serv.anonymize();

        assertEquals(numberOfRecords, serv.getProgress().getTotalitemstodo());
        assertEquals(numberOfRecords, serv.getProgress().getTotalitemscompleted());

        return anonymizerService;
    }

}
