package com.rolfje.anonimatron.configuration;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.file.CsvFileReader;
import com.rolfje.anonimatron.file.CsvFileWriter;
import org.apache.log4j.Logger;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Configuration {
	private static Logger LOG = Logger.getLogger(Configuration.class);

	private String jdbcurl;
	private String userid;
	private String password;

	private String salt;

	private List<Table> tables;
	private List<DataFile> files;
	private List<String> anonymizerClasses;
	private List<String> fileFilters;
	private boolean dryrun = false;
	public boolean isDryrun() {
		return dryrun;
	}

	public void setDryrun(boolean dryrun) {
		this.dryrun = dryrun;
	}

	public static Configuration readFromFile(String filename) throws Exception {
		Mapping mapping = getMapping();
		Unmarshaller unmarshaller = new Unmarshaller(mapping);

		File file = new File(filename);
		Reader reader = new FileReader(file);
		Configuration configuration = (Configuration)unmarshaller.unmarshal(reader);
		LOG.info("Configuration read from " + file.getAbsoluteFile());
		return configuration;
	}

	private static Mapping getMapping() throws IOException, MappingException {
		URL url = Configuration.class.getResource("castor-config-mapping.xml");
		Mapping mapping = new Mapping();
		mapping.loadMapping(url);
		return mapping;
	}

	public static String getDemoConfiguration() throws Exception {
		Mapping mapping = getMapping();

		StringWriter stringWriter = new StringWriter();
		Marshaller marshaller = new Marshaller(stringWriter);
		// I have no idea why this does not work, so I added a castor.propeties
		// file in the root as workaround.
		// marshaller.setProperty("org.exolab.castor.indent", "true");
		marshaller.setMapping(mapping);
		marshaller.marshal(createConfiguration());
		return stringWriter.toString();
	}

	public void setAnonymizerClasses(List<String> anonymizerClasses) {
		this.anonymizerClasses = anonymizerClasses;
	}

	public List<String> getAnonymizerClasses() {
		return anonymizerClasses;
	}

	public String getJdbcurl() {
		return jdbcurl;
	}

	public void setJdbcurl(String jdbcurl) {
		this.jdbcurl = jdbcurl;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public void setFiles(List<DataFile> files) {
		this.files = files;
	}

	public List<DataFile> getFiles() {
		return files;
	}

	public List<String> getFileFilters() {
		return fileFilters;
	}

	public void setFileFilters(List<String> fileFilters) {
		this.fileFilters = fileFilters;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	private static Configuration createConfiguration() throws Exception {
		Configuration conf = new Configuration();

		conf.setSalt("examplesalt");

		List<String> anonymizers = new ArrayList<String>();
		anonymizers.add("my.demo.java.SmurfAnonymizer");
		anonymizers.add("org.sf.anonimatron.CommunityAnonymizer");
		conf.setAnonymizerClasses(anonymizers);

		List<Table> tables = new ArrayList<Table>();
		tables.add(getTable("CUSTOM_TYPES_TABLE", getColumns()));
		tables.add(getTable("DEFAULT_TYPES_TABLE", getDefaultColumns()));
		tables.add(getDiscriminatorExample());

		conf.setTables(tables);

		List<DataFile> dataFiles = new ArrayList<DataFile>();
		dataFiles.add(getDataFile("mydatafile.in.csv", "mydatafile.out.csv", getFileColumns()));
		dataFiles.add(getDataFile("default_types.in.csv", "default_types.out.csv", getDefaultColumns()));

		conf.setFiles(dataFiles);

		conf.setUserid("userid");
		conf.setPassword("password");
		conf.setJdbcurl("jdbc:oracle:thin:@[HOST]:[PORT]:[SID]");
		return conf;
	}

	private static DataFile getDataFile(String inFile, String outFile, List<Column> columns) {
		DataFile t = new DataFile();
		t.setInFile(inFile);
		t.setReader(CsvFileReader.class.getCanonicalName());

		t.setOutFile(outFile);
		t.setWriter(CsvFileWriter.class.getCanonicalName());

		t.setColumns(columns);
		return t;
	}

	private static Table getDiscriminatorExample() {
		// Build example table with a discriminator
		Table discriminatortable = new Table();
		discriminatortable.setName("DISCRIMINATOR_DEMO_TABLE");

		// The default column contains a "phone number" of some sort
		List<Column> defaultcolumns = new ArrayList<Column>();
		Column defaultColumn = new Column();
		defaultColumn.setName("CONTACTINFO");
		defaultColumn.setType("RANDOMDIGITS");
		defaultcolumns.add(defaultColumn);
		discriminatortable.setColumns(defaultcolumns);

		// The specific column contains an "email address"
		List<Column> discriminatecolumns = new ArrayList<Column>();
		Column discriminateColumn = new Column();
		discriminateColumn.setName("CONTACTINFO");
		discriminateColumn.setType("EMAIL_ADDRESS");
		discriminatecolumns.add(discriminateColumn);

		// This is the discriminator which overrides the phone number with an
		// email address
		Discriminator discriminator = new Discriminator();
		discriminator.setColumnName("CONTACTTYPE");
		discriminator.setValue("email address");
		discriminator.setColumns(discriminatecolumns);

		List<Discriminator> discriminators = new ArrayList<Discriminator>();
		discriminators.add(discriminator);

		discriminatortable.setDiscriminators(discriminators);
		return discriminatortable;
	}

	private static Table getTable(String tablename, List<Column> columns) {
		Table t = new Table();
		t.setName(tablename);
		t.setColumns(columns);
		return t;
	}


	private static List<Column> getFileColumns() throws Exception {
		List<Column> columns = new ArrayList<Column>();

		AnonymizerService as = new AnonymizerService();
		String[] types = as.getCustomAnonymizerTypes().toArray(new String[]{});

		// For csv files, indexed column names are the more common example
		for (int i = 0; i < types.length; i++) {
			Column c = new Column();
			c.setName(String.valueOf(i+1));
			c.setType(types[i]);
			columns.add(c);
		}

		return columns;
	}

	private static List<Column> getColumns() throws Exception {
		List<Column> columns = new ArrayList<Column>();

		AnonymizerService as = new AnonymizerService();
		Set<String> types = as.getCustomAnonymizerTypes();

		for (String type : types) {
			Column c = new Column();
			c.setName("A_" + type.toUpperCase() + "_COLUMN");
			c.setType(type);
			columns.add(c);
		}
		return columns;
	}

	private static List<Column> getDefaultColumns() throws Exception {
		List<Column> columns = new ArrayList<Column>();

		AnonymizerService as = new AnonymizerService();
		Set<String> types = as.getDefaultAnonymizerTypes();

		for (String type : types) {
			Column c = new Column();
			c.setName("A_" + type.toUpperCase().replace('.', '_') + "_COLUMN");
			c.setType(type);
			columns.add(c);
		}
		return columns;
	}
}
