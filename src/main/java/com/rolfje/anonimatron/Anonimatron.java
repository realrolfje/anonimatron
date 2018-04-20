package com.rolfje.anonimatron;

import java.io.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rolfje.anonimatron.anonymizer.Hasher;
import com.rolfje.anonimatron.anonymizer.SynonymCache;
import com.rolfje.anonimatron.file.FileAnonymizerService;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.jdbc.JdbcAnonymizerService;
import org.castor.core.util.StringUtil;

/**
 * Start of a beautiful anonymized new world.
 *
 */
public class Anonimatron {
	public static String VERSION="UNKNOWN";
	private static final String OPT_CONFIGFILE = "config";
	private static final String OPT_SYNONYMFILE = "synonyms";
	private static final String OPT_DRYRUN = "dryrun";

	static {
		try {
			InputStream resourceAsStream = Anonimatron.class.getResourceAsStream("version.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			VERSION = bufferedReader.readLine();
			bufferedReader.close();
			inputStreamReader.close();
			resourceAsStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not determine version. " + e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption(OPT_CONFIGFILE, true,
				"The XML Configuration file describing what to anonymize.");
		options.addOption(OPT_SYNONYMFILE, true,
				"The XML file to read/write synonyms to. "
						+ "If the file does not exist it will be created.");
		options.addOption("configexample", false,
				"Prints out a demo/template configuration file.");
		options.addOption(OPT_DRYRUN, false, "Do not make changes to the database.");

		BasicParser parser = new BasicParser();

		try {
			CommandLine commandline = parser.parse(options, args);
			String configfileName = commandline.getOptionValue(OPT_CONFIGFILE);
			String synonymfileName = commandline.getOptionValue(OPT_SYNONYMFILE);
			boolean dryrun = commandline.hasOption(OPT_DRYRUN);

			if (configfileName != null) {
				anonymize(configfileName, synonymfileName, dryrun);
			} else if (commandline.hasOption("configexample")) {
				printDemoConfiguration();
			} else {
				printHelp(options);
			}
		} catch (UnrecognizedOptionException e) {
			System.err.println(e.getMessage());
			printHelp(options);
		}
	}

	private static void anonymize(String configFile, String synonymFile, boolean dryrun)
			throws Exception, SQLException {
		Configuration config = Configuration.readFromFile(configFile);
		config.setDryrun(dryrun);

		// Load Synonyms from disk if present.
		SynonymCache synonymCache = getSynonymCache(synonymFile);

		// Set salt if we have it.
		String salt = config.getSalt();
		if (salt != null && salt.length() > 0) {
			synonymCache.setHasher(new Hasher(salt));
		}

		// Create Anononymizer service
		AnonymizerService anonymizerService = new AnonymizerService(synonymCache);
		anonymizerService.registerAnonymizers(config.getAnonymizerClasses());

		if (config.getTables() != null && config.getTables().size() > 0) {
			JdbcAnonymizerService jdbcService = new JdbcAnonymizerService(config, anonymizerService);
			jdbcService.anonymize();

		} else if (config.getFiles() != null && config.getFiles().size() > 0) {
			FileAnonymizerService fileService = new FileAnonymizerService(config, anonymizerService);
			fileService.anonymize();

		} else {
			System.err.println("Configuration does not contain <table> or <file> elements. Nothing done.");
		}

		if (synonymFile != null) {
			File file = new File(synonymFile);
			System.out.print("Writing Synonyms to " + file.getAbsolutePath() + " ...");
			anonymizerService.getSynonymCache().toFile(new File(synonymFile));
			System.out.println("[done].");
		}
	}

	private static SynonymCache getSynonymCache(String synonymFile) throws Exception {
		SynonymCache synonymCache = new SynonymCache();

		if (synonymFile != null) {
			File file = new File(synonymFile);
			if (file.exists()) {
				System.out.print("Reading Synonyms from "
						+ file.getAbsolutePath() + " ...");
				synonymCache = SynonymCache.fromFile(file);
				System.out.println("[done].");
			}
		}

		return synonymCache;
	}

	private static void printHelp(Options options) {
		System.out
				.println("\nThis is Anonimatron "+VERSION+", a command line tool to consistently \n"
						+ "replace live data in your database or data files with data data which \n"
						+ "can not easily be traced back to the original data.\n"
						+ "You can use this tool to transform a dump from a production \n"
						+ "database into a large representative dataset you can \n"
						+ "share with your development and test team.\n"
						+ "The tool can also read files with sensitive data and write\n"
						+ "consistently anonymized versions of those files to a different location.\n"
						+ "Use the -configexample command line option to get an idea of\n"
						+ "what your configuration file needs to look like.\n\n");

		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("java -jar anonimatron.jar", options);
	}

	private static void printDemoConfiguration() throws Exception {
		System.out.println("\nSupported Database URL formats:");
		JdbcAnonymizerService s = new JdbcAnonymizerService();

		Map<String, String> supportedJdbc = s.getSupportedDriverURLs();
		int col1width = getTextWidth(supportedJdbc.keySet()) + 2;
		System.out.println(String.format("%1$-" + col1width + "s",
				"Jdbc URL format") + "By Driver");
		for (Entry<String, String> entry : supportedJdbc.entrySet()) {
			String col1 = String.format("%1$-" + col1width + "s",
					entry.getKey());
			System.out.println(col1 + entry.getValue());
		}

		System.out
				.println("\nAnonimatron will try to autodetect drivers which are\n"
						+ "stored in the lib directory. Add you driver there.\n\n");

		System.out.println("Demo configuration file for Anonymatron "+VERSION+":");
		System.out.println(Configuration.getDemoConfiguration());
	}

	private static int getTextWidth(Set<String> texts) {
		int width = 0;
		for (String text : texts) {
			width = Math.max(text.length(), width);
		}
		return width;
	}
}
