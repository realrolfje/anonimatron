package com.rolfje.anonimatron.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import junit.framework.TestCase;


import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides functionality and convenience methods for testing against an
 * in-memory Hsql database
 */
public abstract class AbstractInMemoryHsqlDbTest extends TestCase {
    private static final Logger LOG = LogManager.getLogger(AbstractInMemoryHsqlDbTest.class);
    protected static Connection connection;
    public final static String TEST_DB_URL = "jdbc:hsqldb:mem:tests";
    public final static String TEST_DB_USERID = "testuser";
    public final static String TEST_DB_PASSWORD = "testpassword";

    Set<String> tableNames = new HashSet<>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        try {
            DriverManager.getDriver(TEST_DB_URL);
        } catch (Exception e) {
            LOG.debug("Registering HSQL DB Driver");
            DriverManager.registerDriver(new org.hsqldb.jdbcDriver());

        }

        // Creates the in-memory HSQLDB on the fly
        if (connection == null) {
            connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USERID, TEST_DB_PASSWORD);
            connection.setAutoCommit(true);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        int sessionsWithTransaction = getOpenSessions();

        assertEquals("Some sessions still have running transactions.", 0, sessionsWithTransaction);

        // Drop all tables in the database
        executeSql("DROP SCHEMA PUBLIC CASCADE");

        // Close connection and remove reference to it
        connection.close();
        connection = null;

        super.tearDown();
    }

    public int getOpenSessions() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("select SESSION_ID, TRANSACTION, CURRENT_STATEMENT from information_schema.system_sessions");
        ResultSet resultset = statement.getResultSet();
        int sessionsWithTransaction = 0;
        while (resultset.next()) {
            logCurrentRow(resultset);
            boolean inTransaction = resultset.getBoolean(2);
            // LOG.debug("Teardown sesssion: "resultset.getInt(1)+", "+inTransaction+", "+resultset.getString(3));
            if (inTransaction) {
                sessionsWithTransaction++;
            }

        }
        resultset.close();
        statement.close();
        connection.commit();
        return sessionsWithTransaction;
    }

    private void logCurrentRow(ResultSet resultset) throws SQLException {
        StringBuilder b = new StringBuilder(resultset.getRow());
        b.append(": ");
        for (int i = 1; i <= resultset.getMetaData().getColumnCount(); i++) {
            b.append(resultset.getObject(i));
            b.append(" | ");
        }
        LOG.debug(b.toString());
    }

    /**
     * Executes the given sqlStatement against the current connection to the
     * current in-memory database. Use this method to create tables, for
     * instance.
     *
     * @param sqlStatement the SQL statement to execute.
     * @throws Exception
     */
    protected void executeSql(String sqlStatement) throws Exception {
        try (Statement statement = connection.createStatement()) {
            if (statement.execute(sqlStatement)) {
                statement.getResultSet().close();
            }
        } finally {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        }
    }

    /**
     * Provides a quick an easy way to create a valid configuration against the
     * current in-memory database for the given table and the given columns.
     * Automatically fills in the correct jdbc url, usetname and passwords used
     * to create the in-memory database.
     *
     * @param tableName the name of the table to anonymize
     * @param columns   one or more {@link Column}s to anonymize in this table
     * @return A full, valid configuration pointing to the in-memory database
     * for the given table and columns
     */
    protected Configuration createConfiguration(String tableName, Column... columns) {
        if (columns == null) {
            fail("Did not pass in valid Column list for this test.");
        }

        // Build columns
        List<Column> columnList = new ArrayList<>(Arrays.asList(columns));

        // Build Table
        Table tab = new Table();
        tab.setName(tableName);
        tab.setColumns(columnList);
        List<Table> tables = new ArrayList<>();
        tables.add(tab);

        // Add to configuration
        Configuration config = createConfiguration();
        config.setTables(tables);
        return config;

    }

    protected Configuration createConfiguration() {
        Configuration config = new Configuration();
        config.setJdbcurl(TEST_DB_URL);
        config.setUserid(TEST_DB_USERID);
        config.setPassword(TEST_DB_PASSWORD);
        config.setTables(new ArrayList<>());
        return config;
    }


    protected void addToConfig(Configuration config, String tableName, String columnName, String columnType) {
        Table table = null;
        for (Table cftable : config.getTables()) {
            if (cftable.getName().equals(tableName)) {
                table = cftable;
            }
        }

        if (table == null) {
            table = new Table();
            table.setName(tableName);
            table.setColumns(new ArrayList<>());
            config.getTables().add(table);
        }

        Column c = new Column();
        c.setName(columnName);
        c.setType(columnType);

        table.getColumns().add(c);
    }
}
