package com.rolfje.anonimatron.jdbc;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.anonymizer.ToLowerAnonymizer;
import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.Discriminator;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcAnonymizerServiceTest extends AbstractInMemoryHsqlDbTest {
	private static Logger LOG = Logger.getLogger(JdbcAnonymizerServiceTest.class);

	public void testSimpleStrings() throws Exception {
		// Create a table with some easy testdata
		executeSql("create table TABLE1 (COL1 VARCHAR(200), ID IDENTITY)");
		PreparedStatement p = connection
				.prepareStatement("insert into TABLE1 (COL1) values (?)");
		for (int i = 0; i < 100; i++) {
			p.setString(1, "varcharstring-" + i);
			p.execute();
		}
		p.close();

		// See if the data got inserted
		assertEquals(
			"Data was not inserted.",
			100,
			getResultFromCount("select count(*) from TABLE1 where COL1 like 'varcharstring%'"));

		// Create anonimatron configuration
		Configuration config = super.createConfiguration();
		super.addToConfig(config, "TABLE1", "COL1", null);

		AnonymizerService anonymizerService = anonymize(config, 100);

		File f = File.createTempFile("test", ".xml");
		f.deleteOnExit();
		anonymizerService.getSynonymCache().toFile(f);
		LOG.debug("Synonyms written to " + f.getAbsolutePath());

		// See if the data got anonymized
		assertEquals(
			"Data was not anonymized completely.",
			0,
			getResultFromCount("select count(*) from TABLE1 where COL1 like 'varcharstring%'"));

		assertEquals(
			"Rows dissapeared from the data set.",
			100,
			getResultFromCount("select count(*) from TABLE1 where COL1 not like 'varcharstring%'"));
	}

	public void testTooShortUUID() throws Exception {
		// Create a table with some easy testdata
		executeSql("create table TABLE1 (COL1 VARCHAR(1), ID IDENTITY)");
		executeSql("insert into TABLE1 (COL1) values ('a')");

		// Create anonimatron configuration
		Column col = new Column();
		col.setName("COL1");
		col.setType("UUID");
		Configuration config = super.createConfiguration("TABLE1", col);

		try {
			// Anonymize the data.
			JdbcAnonymizerService serv = new JdbcAnonymizerService(config, new AnonymizerService());
			serv.anonymize();

			fail("Should not be able to store UUID in a 1 character varchar");
		} catch (Exception e) {
			// Test passed.
		}
	}
	
	public void testDryRun() throws Exception {
		executeSql("create table TABLE1 (COL1 VARCHAR(16), ID IDENTITY)");
		executeSql("insert into TABLE1 (COL1) values ('abcdefghijklmnop')");

		// Create anonimatron configuration
		Configuration config = super.createConfiguration();
		super.addToConfig(config, "TABLE1", "COL1", null);
		
		// Make this a dry run
		config.setDryrun(true);

		anonymize(config, 1);
		
		// Check the outcome, nothing should be changed
		Statement statement = connection.createStatement();
		statement.execute("select * from TABLE1 order by ID");
		ResultSet resultset = statement.getResultSet();

		resultset.next();
		assertEquals("abcdefghijklmnop", resultset.getString("COL1"));
		
		resultset.close();
		statement.close();

		// Make this a dry run
		config.setDryrun(false);

		anonymize(config, 1);
		
		// Check the outcome, data should be changed
		statement = connection.createStatement();
		statement.execute("select * from TABLE1 order by ID");
		resultset = statement.getResultSet();

		resultset.next();
		assertFalse("abcdefghijklmnop".equals(resultset.getString("COL1")));
		
		resultset.close();
		statement.close();
	}

	public void testDiscriminators() throws Exception {
		executeSql("create table TABLE1 (id IDENTITY, value1 VARCHAR(100), value2 VARCHAR(100), key VARCHAR(100))");
		executeSql("insert into TABLE1 (value1,value2,key) values ('A','X','NONE')");
		executeSql("insert into TABLE1 (value1,value2,key) values ('B','Y','EMAIL')");
		executeSql("insert into TABLE1 (value1,value2,key) values ('C','Z',null)");

		// Create default column configuration
		Configuration config = super.createConfiguration();
		super.addToConfig(config, "TABLE1", "value1", "TO_LOWER_CASE");
		super.addToConfig(config, "TABLE1", "value2", "TO_LOWER_CASE");
		super.addToConfig(config, "TABLE1", "key", "TO_LOWER_CASE");

		ArrayList<String> anonymizerclasses = new ArrayList<String>();
		anonymizerclasses.add(ToLowerAnonymizer.class.getName());
		config.setAnonymizerClasses(anonymizerclasses);

		// Add discriminator based on key
		Discriminator discriminator = new Discriminator();
		discriminator.setColumnName("key");
		discriminator.setValue("EMAIL");
		Column emailcol = new Column();
		emailcol.setName("value1");
		emailcol.setType("EMAIL_ADDRESS");
		List<Column> emailcols = new ArrayList<Column>();
		emailcols.add(emailcol);
		discriminator.setColumns(emailcols);

		Discriminator discriminator2 = new Discriminator();
		discriminator2.setColumnName("key");
		discriminator2.setValue(null);
		Column uuidcol = new Column();
		uuidcol.setName("value1");
		uuidcol.setType("UUID");
		List<Column> uuidcolscols = new ArrayList<Column>();
		uuidcolscols.add(uuidcol);
		discriminator2.setColumns(uuidcolscols);

		List<Discriminator> discriminators = new ArrayList<Discriminator>();
		discriminators.add(discriminator);
		discriminators.add(discriminator2);
		config.getTables().get(0).setDiscriminators(discriminators);

		anonymize(config, 3);

		LOG.debug("Table contents for TABLE1: \n" + resultSetAsString("select * from TABLE1 order by ID"));

		// Check the outcome
		Statement statement = connection.createStatement();
		statement.execute("select * from TABLE1 order by ID");
		ResultSet resultset = statement.getResultSet();

		resultset.next();
		assertEquals("a", resultset.getString("value1"));
		assertEquals("x", resultset.getString("value2"));
		assertEquals("none", resultset.getString("key"));

		resultset.next();
		assertTrue(resultset.getString("value1").contains("@example.com"));
		assertEquals("y", resultset.getString("value2"));
		assertEquals("email", resultset.getString("key"));

		resultset.next();
		assertTrue(resultset.getString("value1").contains("-"));
		assertEquals("z", resultset.getString("value2"));
		assertNull(resultset.getString("key"));

		resultset.close();
		statement.close();
	}

	public void testProgressForMultipleTables() throws Exception {
		executeSql("create table TABLE1 (COL1 VARCHAR(200), ID IDENTITY)");
		PreparedStatement p = connection
				.prepareStatement("insert into TABLE1 (COL1) values (?)");
		for (int i = 0; i < 1251; i++) {
			p.setString(1, "varcharstring-" + i);
			p.execute();
		}
		p.close();

		executeSql("create table TABLE2 (COL1 VARCHAR(200), ID IDENTITY)");
		p = connection
				.prepareStatement("insert into TABLE2 (COL1) values (?)");
		for (int i = 0; i < 1251; i++) {
			p.setString(1, "varcharstring-" + i);
			p.execute();
		}
		p.close();

		executeSql("create table TABLE3 (COL1 VARCHAR(200), ID IDENTITY)");
		p = connection
				.prepareStatement("insert into TABLE3 (COL1) values (?)");
		for (int i = 0; i < 1251; i++) {
			p.setString(1, "varcharstring-" + i);
			p.execute();
		}
		p.close();

		// Create anonimatron configuration
		Configuration config = super.createConfiguration();
		addToConfig(config, "TABLE1", "COL1", null);
		addToConfig(config, "TABLE2", "COL1", null);
		addToConfig(config, "TABLE3", "COL1", null);

		anonymize(config, 3753);
	}

	public void testDataTypes() throws Exception {
		executeSql("create table TABLE1 (COL1 DATE, ID IDENTITY)");
		PreparedStatement p = connection
				.prepareStatement("insert into TABLE1 (COL1) values (?)");
		for (int i = 0; i < 2; i++) {
			p.setDate(1, new Date(Math.round(System.currentTimeMillis() * Math.random())));
			p.execute();
		}
		p.close();

		Configuration config = super.createConfiguration();
		super.addToConfig(config, "TABLE1", "COL1", null);

		anonymize(config, 2);
	}

	private AnonymizerService anonymize(Configuration config, int numberOfRecords) throws Exception, SQLException {
		// Anonymize the data.
		AnonymizerService anonymizerService = new AnonymizerService();
		anonymizerService.registerAnonymizers(config.getAnonymizerClasses());
		JdbcAnonymizerService serv = new JdbcAnonymizerService(config, anonymizerService);
		serv.anonymize();

		assertEquals(numberOfRecords, serv.getProgress().getTotalitemstodo());
		assertEquals(numberOfRecords, serv.getProgress().getTotalitemscompleted());

		return anonymizerService;
	}

	private String resultSetAsString(String select) throws SQLException {
		Statement statement = null;
		ResultSet resultset = null;

		StringBuilder sbuilder;
		try {
			statement = connection.createStatement();
			statement.execute(select);
			resultset = statement.getResultSet();
			ResultSetMetaData rsmd = resultset.getMetaData();
			int numCols = rsmd.getColumnCount();

			sbuilder = new StringBuilder();

			for (int i = 1; i <= numCols; i++) {
				sbuilder.append(rsmd.getColumnName(i));
				sbuilder.append(";");
			}

			sbuilder.append("\n");

			while (resultset.next()) {
				for (int i = 1; i <= numCols; i++) {
					sbuilder.append(resultset.getObject(i));
					sbuilder.append(";");
				}
				sbuilder.append("\n");
			}
		} finally {
			resultset.close();
			statement.close();
		}

		return sbuilder.toString();
	}

	private int getResultFromCount(String sql) throws Exception, SQLException {
		Statement statement = connection.createStatement();
		statement.execute(sql);
		ResultSet resultset = statement.getResultSet();
		resultset.next();
		int count = resultset.getInt(1);
		resultset.close();
		statement.close();
		return count;
	}
}
