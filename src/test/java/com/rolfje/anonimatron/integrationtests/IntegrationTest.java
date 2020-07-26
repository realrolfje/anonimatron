package com.rolfje.anonimatron.integrationtests;

import com.rolfje.anonimatron.Anonimatron;
import com.rolfje.anonimatron.jdbc.AbstractInMemoryHsqlDbTest;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IntegrationTest extends AbstractInMemoryHsqlDbTest {
	Logger LOG = Logger.getLogger(IntegrationTest.class);

	private File configFile;
	private File synonymFile;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configFile = createConfigurationFile();
		synonymFile = File.createTempFile("anonimatron-synonyms", ".xml");

		createDatabase();
	}

	@Override
	protected void tearDown() throws Exception {
		assertTrue("Could not delete temporary configuration.", configFile.delete());
		assertTrue("Could not delete temporary synonym file.", synonymFile.delete());
		super.tearDown();
	}

	public void testAnonimatron() throws Exception {
		runAnonimatron(configFile.getAbsolutePath(), synonymFile.getAbsolutePath());
		resultSetIsAnonymized("TABLE1", "COL1");
		resultSetIsAnonymized("TESTSCHEMA.TABLE2", "COL1");
	}

	private void createDatabase() throws Exception {
		executeSql("create table TABLE1 (COL1 VARCHAR(200), ID IDENTITY)");
		PreparedStatement p = connection
				.prepareStatement("insert into TABLE1 (COL1) values (?)");
		for (int i = 0; i < 100; i++) {
			p.setString(1, "varcharstring-" + i);
			p.execute();
		}

		executeSql("create schema TESTSCHEMA authorization DBA");
		executeSql("create table TESTSCHEMA.TABLE2 (COL1 VARCHAR(200), ID IDENTITY)");
		p = connection
				.prepareStatement("insert into TESTSCHEMA.TABLE2 (COL1) values (?)");
		for (int i = 0; i < 100; i++) {
			p.setString(1, "varcharstring-" + i);
			p.execute();
		}

		LOG.info("Created test database.");
	}

	private void resultSetIsAnonymized(String table, String column) throws Exception {
		PreparedStatement preparedStatement = connection.prepareStatement("select * from " + table);
		preparedStatement.execute();
		ResultSet resultSet = preparedStatement.getResultSet();
		int rowcount = 0;
		while (resultSet.next()) {
			rowcount++;
			String value = resultSet.getString(column);
			assertFalse(value.startsWith("varcharstring-"));
			assertTrue(value.endsWith("@example.com"));
		}
		assertEquals("Not all rows accounted for", 100, rowcount);
	}

	/**
	 * Copies the integrationconfig.xml file from the classpath into a
	 * temportary system file.
	 * 
	 * @return
	 * @throws Exception
	 */
	private File createConfigurationFile() throws Exception {
		LOG.debug("Copying " + IntegrationTest.class.getResource("integrationconfig.xml") + " to a tempfile.");
		InputStream stream = IntegrationTest.class.getResourceAsStream("integrationconfig.xml");
		assertNotNull(stream);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

		File tempConfig = File.createTempFile("anonimatron-config", ".xml");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempConfig));

		while (reader.ready()) {
			writer.write(reader.readLine());
		}

		reader.close();
		writer.flush();
		writer.close();

		return tempConfig;
	}

	private void runAnonimatron(String configFile, String synonymFile)
			throws Exception {
		String[] arguments = new String[] { "-config", configFile, "-synonyms",
				synonymFile };

		Anonimatron.main(arguments);
	}
}
