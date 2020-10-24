package com.rolfje.anonimatron.jdbc;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.anonymizer.Prefetcher;
import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.Discriminator;
import com.rolfje.anonimatron.configuration.Table;
import com.rolfje.anonimatron.progress.Progress;
import com.rolfje.anonimatron.progress.ProgressPrinter;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class JdbcAnonymizerService {
    Logger LOG = Logger.getLogger(JdbcAnonymizerService.class);

    private Configuration config;
    private Connection connection;
    private AnonymizerService anonymizerService;
    private Progress progress;

    private Map<String, String> supportedJdbcUrls = new HashMap<>();

    public JdbcAnonymizerService() {
        registerDrivers();
    }

    public JdbcAnonymizerService(Configuration config, AnonymizerService anonymizerService) throws Exception {
        this();
        this.config = config;
        this.anonymizerService = anonymizerService;
        setConnection();
    }

    public void printConfigurationInfo() {
        System.out.println("\nAnonymization process started\n");
        System.out.println("Jdbc url      : " + config.getJdbcurl());
        System.out.println("Database user : " + config.getUserid());
        System.out.println("To do         : " + config.getTables().size()
                + " tables.\n");
    }

    public void anonymize() throws SQLException {
        printConfigurationInfo();

        // Get records to do
        List<Table> tables = config.getTables();
        int totalRows = 0;
        for (Table table : tables) {
            // Get the total number of records to process
            long numberofRows = getRowCount(table);
            table.setNumberOfRows(numberofRows);
            totalRows += numberofRows;
            LOG.info("Table " + table.getName() + " has " + numberofRows + " rows to process.");
        }

        progress = new Progress();
        progress.setTotalitemstodo(totalRows);

        ProgressPrinter printer = new ProgressPrinter(progress);
        printer.setPrintIntervalMillis(1000);
        printer.start();

        try {
            for (Table table : tables) {
                printer.setMessage("Pre-scanning table '" + table.getName()
                        + "', total progress ");
                preScanTable(table);
            }

            progress.reset();

            for (Table table : tables) {
                printer.setMessage("Anonymizing table '" + table.getName()
                        + "', total progress ");
                anonymizeTableInPlace(table);
            }
        } finally {
            printer.stop();
        }

        System.out.println("\nAnonymization process completed.\n");
    }

    private void setConnection() throws SQLException {
        String jdbcurl = config.getJdbcurl();
        String userid = config.getUserid();
        String password = config.getPassword();

        connection = DriverManager.getConnection(jdbcurl, userid, password);
        connection.setAutoCommit(false);

        LOG.info("Conected to '" + jdbcurl + "' with user '" + userid + "'.");
    }

    /**
     * Runs through the table data, feeds it to the Synonyms which implement
     * {@link Prefetcher}, so that they can analyze the source data to base
     * their synonym algorithm on.
     */
    private void preScanTable(Table table) {
        ColumnWorker worker = (results, column, databaseColumnValue) -> anonymizerService.prepare(column.getType(),
                databaseColumnValue);

        processTableColumns(table, worker, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    private void anonymizeTableInPlace(Table table) {
        ColumnWorker worker = (results, column, databaseColumnValue) -> {
            Synonym synonym = anonymizerService.anonymize(column,
                    databaseColumnValue);

            if (results.getConcurrency() == ResultSet.CONCUR_UPDATABLE) {
                // Update the contents of this row with the given Synonym
                results.updateObject(column.getName(), synonym.getTo());
            }

            return true;
        };

        int resultsetConcurrency = getAnonimizerResultSetConcurrency();

        processTableColumns(table, worker, ResultSet.TYPE_FORWARD_ONLY, resultsetConcurrency);
    }

    private int getAnonimizerResultSetConcurrency() {
        if (config.isDryrun()) {
            return ResultSet.CONCUR_READ_ONLY;
        } else {
            return ResultSet.CONCUR_UPDATABLE;
        }
    }

    private void processTableColumns(Table table, final ColumnWorker columnWorker, int resultSetType, int resultSetConcurrency) {
        // Create an updatable resultset for the rows.
        Statement statement = null;
        ResultSet results = null;
        long rowsleft = table.getNumberOfRows();
        try {
            NDC.push("Table '" + table.getName() + "'");
            String select = getSelectStatement(table);
            LOG.debug(select);

            statement = connection.createStatement(resultSetType, resultSetConcurrency);
            if (table.getFetchSize() != null) {
                statement.setFetchSize(table.getFetchSize());
            }
            statement.execute(select);
            results = statement.getResultSet();
            ResultSetMetaData resultsMetaData = results.getMetaData();

            if (results.getConcurrency() != resultSetConcurrency) {
                LOG.warn("The resultset concurrency was " + results.getConcurrency() + " while we tried to set it to " + resultSetConcurrency);
            }

            boolean processNextRecord = true;
            while (results.next() && processNextRecord) {
                Collection<Column> columnsAsList = getDiscriminatedColumnConfiguration(table, results);

                /*
                 * If any of the calls to the worker indicate that
                 * we need to continue, we'll fetch the next result (see below).
                 * If we had no columns to do because we had no columns and no discriminators firing,
                 * we assume that we do need to process the next record.
                 * TODO This is now a strange way to stop the loop on synonym depletion and needs refactoring.
                 */
                processNextRecord = false || columnsAsList.isEmpty();

                for (Column column : columnsAsList) {
                    // Build a synonym for each column in this row
                    NDC.push("Column '" + column.getName() + "'");

                    String columnType = column.getType();
                    if (columnType == null) {
                        columnType = resultsMetaData.getColumnClassName(results.findColumn(column.getName()));
                        column.setType(columnType);
                    }

                    NDC.push("Type '" + columnType + "'");

                    int columnDisplaySize = resultsMetaData.getColumnDisplaySize(results.findColumn(column.getName()));
                    column.setSize(columnDisplaySize);

                    Object databaseColumnValue = results.getObject(column.getName());

                    /* If any call returns true, process the next record. */
                    processNextRecord = columnWorker.processColumn(results, column, databaseColumnValue)
                            || processNextRecord;

                    NDC.pop(); // type
                    NDC.pop(); // column
                }

                // Do not update for read-only resultsets
                if (results.getConcurrency() == ResultSet.CONCUR_UPDATABLE) {
                    results.updateRow();
                }

                rowsleft--;
                progress.incItemsCompleted(1);
            }

            /*
             * Makes sure that we update the progress monitor even if we have
             * broken out of the loop, effectively skipping the rest of the rows
             */
            progress.incItemsCompleted(rowsleft);

            if (!processNextRecord) {
                LOG.debug("The Anonimizer service has indicated that it can skip the rest of table "
                        + table.getName() + " in this pass.");
            }

        } catch (Exception e) {
            LOG.fatal("Anonymyzation stopped because of fatal error.", e);
            throw new RuntimeException(e);
        } finally {
            NDC.remove();
            commitAndClose(connection, statement, results);
        }
    }

    private void commitAndClose(Connection conn, Statement statement, ResultSet results) {
        try {
            /*
             * TODO: Figure out why we need to do a commit even if the resultset
             * is *not* updatable.
             */
            // if (!conn.getAutoCommit() && results.getConcurrency() ==
            // ResultSet.CONCUR_UPDATABLE) {

            if (!conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (SQLException e) {
            LOG.error("Could not commit", e);
        }

        try {
            if (results != null) results.close();
        } catch (SQLException e) {
            LOG.error("Could not close resultset.", e);
        }

        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {
            LOG.error("Could not close statement.", e);
        }
    }

    private Collection<Column> getDiscriminatedColumnConfiguration(Table table, ResultSet results) throws SQLException {
        List<Discriminator> discriminators = table.getDiscriminators();
        if (discriminators == null) {
            // No discriminators, carry on with default columns
            return table.getColumns();
        }

        // Get default columns as a map
        Map<String, Column> columnsAsMap = Table.getColumnsAsMap(table.getColumns());

        // Overwrite/add columns in the map based on discriminators
        for (Discriminator discriminator : discriminators) {

            String columnName = discriminator.getColumnName();
            if (columnName == null) {
                throw new IllegalArgumentException("A discriminator in the configuration for table " + table.getName() + " did not concain a column name.");
            }
            Object value = results.getObject(columnName);

            if ((discriminator.getValue() != null && discriminator.getValue().equals(value))
                    || (discriminator.getValue() == null) && (value == null)) {
                // Overload standard column definition
                Map<String, Column> discrColumns = Table.getColumnsAsMap(discriminator.getColumns());
                columnsAsMap.putAll(discrColumns);
            }
        }

        return columnsAsMap.values();
    }

    private long getRowCount(Table table) throws SQLException {
        String select = "select count(1) from " + table.getName();
        Statement statement = null;
        ResultSet results = null;

        try {
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            statement.execute(select);
            results = statement.getResultSet();

            if (results.getConcurrency() != ResultSet.CONCUR_READ_ONLY) {
                LOG.warn("Could not set this resultset to be read-only.");
            }

            results.next();
            /*
             * Please note that "getLong" does not work correctly for large
             * numbers in MySQL. That's why we're using a BigDecimal and get the
             * long value from that.
             */
            return results.getBigDecimal(1).longValue();
        } finally {
            commitAndClose(connection, statement, results);
        }
    }

    private String getSelectStatement(Table table) throws SQLException {
        Set<String> columnNames = new HashSet<>();
        if (table.getColumns() != null) {
            for (Column column : table.getColumns()) {
                columnNames.add(column.getName());
            }
        }

        if (table.getDiscriminators() != null) {
            // Add all columns involved in discriminator selection and its column definitions
            for (Discriminator discriminator : table.getDiscriminators()) {
                columnNames.add(discriminator.getColumnName());
                columnNames.addAll(
                        discriminator.getColumns()
                                .stream()
                                .map(Column::getName)
                                .collect(Collectors.toList())
                );
            }
        }

        String primaryKeys = getPrimaryKeys(table, columnNames);

        String select = "select " + primaryKeys;

        for (String columnName : columnNames) {
            select += columnName + ", ";
        }

        select = select.substring(0, select.lastIndexOf(", "));
        select += " from " + table.getName();
        return select;
    }

    /**
     * @param table       The table to fetch the primary keys for.
     * @param columnNames The column names in the configuration that need to be anonimyzed or are
     *                    used as a discriminator column
     * @return A comma separated list of primary keys which are not part of any discriminator
     * or anonimyzation column.
     * @throws SQLException
     * @throws RuntimeException When there is a problem with the configuration or precondition.
     */
    private String getPrimaryKeys(Table table, Set<String> columnNames) throws SQLException {

        String schema = null;
        String tablename = table.getName();
        String[] split = table.getName().split("\\.");
        if (split.length == 2) {
            schema = split[0];
            tablename = split[1];
        }

        try (ResultSet resultset = connection.getMetaData().getPrimaryKeys(connection.getCatalog(), schema, tablename)) {
            String primaryKeys = "";
            while (resultset.next()) {
                String columnName = resultset.getString("COLUMN_NAME");
                if (columnNames.contains(columnName)) {
                    String msg = "Column " + columnName + " in table " + table.getName()
                            + " can not be anonimyzed because it is also a primary key.";
                    LOG.error(msg);
                    throw new RuntimeException(msg);
                } else {
                    primaryKeys += columnName + ", ";
                }
            }

            if (primaryKeys.length() < 1) {
                String msg = "Table " + table.getName() + " does not contain a primary key and can not be anonymyzed.";
                LOG.error(msg);
                throw new RuntimeException(msg);
            }

            return primaryKeys;
        }
    }

    /**
     * Registers all database drivers which can be found on the classpath
     */
    private void registerDrivers() {
        // Array of arrays. Each array is a jdb-url and classname combo.
        String[][] drivers = {
                {"jdbc:oracle:thin:@[HOST]:[PORT]:[SID]", "oracle.jdbc.driver.OracleDriver"},
                {"jdbc:db2://[HOST]:[PORT]/[DB]", "COM.ibm.db2.jdbc.app.DB2Driver"},
                {"jdbc:weblogic:mssqlserver4:[DB]@[HOST]:[PORT]", "weblogic.jdbc.mssqlserver4.Driver"},
                {"jdbc:pointbase://embedded[:[PORT]]/[DB]", "com.pointbase.jdbc.jdbcUniversalDriver"},
                {"jdbc:cloudscape:[DB]", "COM.cloudscape.core.JDBCDriver"},
                {"jdbc:rmi://[HOST]:[PORT]/jdbc:cloudscape:[DB]", "RmiJdbc.RJDriver"},
                {"jdbc:firebirdsql:[//[HOST][:[PORT]]/][DB]", "org.firebirdsql.jdbc.FBDriver"},
                {"jdbc:ids://[HOST]:[PORT]/conn?dsn='[ODBC_DSN_NAME]'", "ids.sql.IDSDriver"},
                {"jdbc:informix-sqli://[HOST]:[PORT]/[DB]:INFORMIXSERVER=[SERVER_NAME]", "com.informix.jdbc.IfxDriver"},
                {"jdbc:idb:[DB]", "jdbc.idbDriver"},
                {"jdbc:idb:[DB]", "org.enhydra.instantdb.jdbc.idbDriver"},
                {"jdbc:interbase://[HOST]/[DB]", "interbase.interclient.Driver"},
                {"jdbc:HypersonicSQL:[DB]", "hSql.hDriver"},
                {"jdbc:HypersonicSQL:[DB]", "org.hsqldb.jdbcDriver"},
                {"jdbc:JTurbo://[HOST]:[PORT]/[DB]", "com.ashna.jturbo.driver.Driver"},
                {"jdbc:inetdae:[HOST]:[PORT]?database=[DB]", "com.inet.tds.TdsDriver"},
                {"jdbc:microsoft:sqlserver://[HOST]:[PORT][;DatabaseName=[DB]]", "com.microsoft.sqlserver.jdbc.SQLServerDriver"},
                {"jdbc:sqlserver://[HOST]:[PORT][;DatabaseName=[DB]]", "com.microsoft.sqlserver.jdbc.SQLServerDriver"},
                {"jdbc:mysql://[HOST]:[PORT]/[DB]", "org.gjt.mm.mysql.Driver"},
                {"jdbc:oracle:oci8:@[SID]", "oracle.jdbc.driver.OracleDriver"},
                {"jdbc:oracle:oci:@[SID]", "oracle.jdbc.driver.OracleDriver"},
                {"jdbc:postgresql://[HOST]:[PORT]/[DB]", "postgresql.Driver"},
                {"jdbc:postgresql://[HOST]:[PORT]/[DB]", "org.postgresql.Driver"},
                {"jdbc:sybase:Tds:[HOST]:[PORT]", "com.sybase.jdbc.SybDriver"},
                {"jdbc:sybase:Tds:[HOST]:[PORT]/[DB]", "net.sourceforge.jtds.jdbc.Driver"},
        };

        for (String[] urlClassCombo : drivers) {
            String driverURL = urlClassCombo[0];
            String driverClassName = urlClassCombo[1];
            try {
                @SuppressWarnings("rawtypes")
                Class driverClass = Class.forName(driverClassName);
                DriverManager
                        .registerDriver((Driver) driverClass.newInstance());
                supportedJdbcUrls.put(driverURL, driverClassName);
            } catch (Exception e) {
                // Skipping this driver.
            }
        }
    }

    public Map<String, String> getSupportedDriverURLs() {
        return Collections.unmodifiableMap(supportedJdbcUrls);
    }

    public Progress getProgress() {
        return progress;
    }
}
