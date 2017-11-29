package com.rolfje.anonimatron.integrationtests;

import com.rolfje.anonimatron.Anonimatron;
import com.rolfje.anonimatron.jdbc.AbstractInMemoryHsqlDbTest;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.PreparedStatement;

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
	}

	private void createDatabase() throws Exception {
		executeSql("create table TABLE1 (COL1 VARCHAR(200), ID IDENTITY)");
		PreparedStatement p = connection
				.prepareStatement("insert into TABLE1 (COL1) values (?)");
		for (int i = 0; i < 100; i++) {
			p.setString(1, "varcharstring-" + i);
			p.execute();
		}

		LOG.info("Created test database.");
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
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

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
