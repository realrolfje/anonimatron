package com.rolfje.anonimatron.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.anonymizer.Prefetcher;
import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.Discriminator;
import com.rolfje.anonimatron.configuration.Table;
import com.rolfje.anonimatron.progress.Progress;
import com.rolfje.anonimatron.progress.ProgressPrinter;
import com.rolfje.anonimatron.synonyms.Synonym;

public class JdbcAnonymizerService {
	Logger LOG = Logger.getLogger(JdbcAnonymizerService.class);

	private Configuration config;
	private Connection connection;
	private AnonymizerService anonymizerService;
	private Progress progress;

	private Map<String, String> supportedJdbcUrls = new HashMap<String, String>();

	public JdbcAnonymizerService() throws Exception {
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
	 * 
	 * @param table
	 * @throws SQLException
	 */
	private void preScanTable(Table table) throws SQLException {
		ColumnWorker worker = new ColumnWorker() {
			@Override
			public boolean processColumn(ResultSet results, Column column, String columnType,
					Object databaseColumnValue, int columnDisplaySize) throws SQLException {
				return anonymizerService.prepare(columnType,
					databaseColumnValue);
			}
		};

		processTableColumns(table, worker, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	private void anonymizeTableInPlace(Table table) throws SQLException {
		ColumnWorker worker = new ColumnWorker() {
			@Override
			public boolean processColumn(ResultSet results, Column column, String columnType,
					Object databaseColumnValue, int columnDisplaySize) throws SQLException {
				Synonym synonym = anonymizerService.anonymize(columnType,
					databaseColumnValue,
					columnDisplaySize);

				if (results.getConcurrency() == ResultSet.CONCUR_UPDATABLE) {
					// Update the contents of this row with the given Synonym
					results.updateObject(column.getName(), synonym.getTo());
				}

				return true;
			}
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
				 * Assume ready. if any of the calls to the worker indicate that
				 * we need to continue, we'll fetch the next result (see below(
				 */
				processNextRecord = false;

				for (Column column : columnsAsList) {
					// Build a synonym for each column in this row
					NDC.push("Column '" + column.getName() + "'");

					String columnType = column.getType();
					if (columnType == null) {
						columnType = resultsMetaData.getColumnClassName(results.findColumn(column.getName()));
					}

					NDC.push("Type '" + columnType + "'");

					Object databaseColumnValue = results.getObject(column.getName());
					int columnDisplaySize = resultsMetaData.getColumnDisplaySize(results.findColumn(column.getName()));

					/* If any call returns true, process the next record. */
					processNextRecord = columnWorker.processColumn(results, column, columnType, databaseColumnValue, columnDisplaySize)
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

		// Overwrite columns in the map based on discriminators
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
		Set<String> columnNames = new HashSet<String>();
		for (Column column : table.getColumns()) {
			columnNames.add(column.getName());
		}

		if (table.getDiscriminators() != null) {
			for (Discriminator discriminator : table.getDiscriminators()) {
				columnNames.add(discriminator.getColumnName());
			}
		}

		ResultSet results = connection.getMetaData().getPrimaryKeys(null, null, table.getName());
		String primaryKeys = "";
		while (results.next()) {
			String columnName = results.getString("COLUMN_NAME");
			if (!columnNames.contains(columnName)) {
				primaryKeys += columnName + ", ";
			}
		}
		results.close();

		if (primaryKeys.length() < 1) {
			String msg = "Table " + table.getName() + " does not contain a primary key and can not be anonymyzed.";
			LOG.error(msg);
			throw new RuntimeException(msg);
		}

		String select = "select " + primaryKeys;

		for (String columnName : columnNames) {
			select += columnName + ", ";
		}

		select = select.substring(0, select.lastIndexOf(", "));
		select += " from " + table.getName();
		return select;
	}

	/**
	 * Registers all database drivers which can be found on the classpath
	 */
	private void registerDrivers() {
		Map<String, String> drivers = new HashMap<String, String>();
		drivers.put("jdbc:oracle:thin:@[HOST]:[PORT]:[SID]",
			"oracle.jdbc.driver.OracleDriver");
		drivers.put("jdbc:db2://[HOST]:[PORT]/[DB]",
			"COM.ibm.db2.jdbc.app.DB2Driver");
		drivers.put("jdbc:weblogic:mssqlserver4:[DB]@[HOST]:[PORT]",
			"weblogic.jdbc.mssqlserver4.Driver");
		drivers.put("jdbc:pointbase://embedded[:[PORT]]/[DB]",
			"com.pointbase.jdbc.jdbcUniversalDriver");
		drivers.put("jdbc:cloudscape:[DB]", "COM.cloudscape.core.JDBCDriver");
		drivers.put("jdbc:rmi://[HOST]:[PORT]/jdbc:cloudscape:[DB]",
			"RmiJdbc.RJDriver");
		drivers.put("jdbc:firebirdsql:[//[HOST][:[PORT]]/][DB]",
			"org.firebirdsql.jdbc.FBDriver");
		drivers.put("jdbc:ids://[HOST]:[PORT]/conn?dsn='[ODBC_DSN_NAME]'",
			"ids.sql.IDSDriver");
		drivers.put(
			"jdbc:informix-sqli://[HOST]:[PORT]/[DB]:INFORMIXSERVER=[SERVER_NAME]",
			"com.informix.jdbc.IfxDriver");
		drivers.put("jdbc:idb:[DB]", "jdbc.idbDriver");
		drivers.put("jdbc:idb:[DB]", "org.enhydra.instantdb.jdbc.idbDriver");
		drivers.put("jdbc:interbase://[HOST]/[DB]",
			"interbase.interclient.Driver");
		drivers.put("jdbc:HypersonicSQL:[DB]", "hSql.hDriver");
		drivers.put("jdbc:HypersonicSQL:[DB]", "org.hsqldb.jdbcDriver");
		drivers.put("jdbc:JTurbo://[HOST]:[PORT]/[DB]",
			"com.ashna.jturbo.driver.Driver");
		drivers.put("jdbc:inetdae:[HOST]:[PORT]?database=[DB]",
			"com.inet.tds.TdsDriver");
		drivers.put(
			"jdbc:microsoft:sqlserver://[HOST]:[PORT][;DatabaseName=[DB]]",
			"com.microsoft.sqlserver.jdbc.SQLServerDriver");
		drivers.put("jdbc:mysql://[HOST]:[PORT]/[DB]",
			"org.gjt.mm.mysql.Driver");
		drivers.put("jdbc:oracle:oci8:@[SID]",
			"oracle.jdbc.driver.OracleDriver");
		drivers.put("jdbc:oracle:oci:@[SID]", "oracle.jdbc.driver.OracleDriver");
		drivers.put("jdbc:postgresql://[HOST]:[PORT]/[DB]", "postgresql.Driver");
		drivers.put("jdbc:postgresql://[HOST]:[PORT]/[DB]",
			"org.postgresql.Driver");
		drivers.put("jdbc:sybase:Tds:[HOST]:[PORT]",
			"com.sybase.jdbc.SybDriver");
		drivers.put("jdbc:sybase:Tds:[HOST]:[PORT]/[DB]",
				"net.sourceforge.jtds.jdbc.Driver");


		for (Entry<String, String> driverEntry : drivers.entrySet()) {
			String driverURL = driverEntry.getKey();
			String driverClassName = driverEntry.getValue();
			try {
				@SuppressWarnings("rawtypes")
				Class driverClass = Class.forName(driverClassName);
				DriverManager
						.registerDriver((Driver)driverClass.newInstance());
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
