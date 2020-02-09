package com.rolfje.anonimatron;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.anonymizer.Hasher;
import com.rolfje.anonimatron.anonymizer.SynonymCache;
import com.rolfje.anonimatron.commandline.CommandLine;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.file.FileAnonymizerService;
import com.rolfje.anonimatron.jdbc.JdbcAnonymizerService;
import org.apache.commons.cli.UnrecognizedOptionException;

import java.io.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Start of a beautiful anonymized new world.
 *
 */
public class Anonimatron {
	public static String VERSION = "UNKNOWN";

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
		try {
			CommandLine commandLine = new CommandLine(args);

			if (commandLine.getConfigfileName() != null) {
				Configuration config = getConfiguration(commandLine);
				anonymize(config, commandLine.getSynonymfileName());

			} else if (commandLine.isConfigExample()) {
				printDemoConfiguration();

			} else {
				CommandLine.printHelp();
			}
		} catch (UnrecognizedOptionException e) {
			System.err.println(e.getMessage());
			CommandLine.printHelp();
		}
	}

	private static Configuration getConfiguration(CommandLine commandLine) throws Exception {
		// Load configuration
		Configuration config = Configuration.readFromFile(commandLine.getConfigfileName());
		if (commandLine.getJdbcurl() != null) {
			config.setJdbcurl(commandLine.getJdbcurl());
		}
		if (commandLine.getUserid() != null) {
			config.setUserid(commandLine.getUserid());
		}
		if (commandLine.getPassword() != null) {
			config.setPassword(commandLine.getPassword());
		}
		config.setDryrun(commandLine.isDryrun());
		return config;
	}

	private static void anonymize(Configuration config, String synonymFile) throws Exception {
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

		if (config.getTables() != null && !config.getTables().isEmpty()) {
			JdbcAnonymizerService jdbcService = new JdbcAnonymizerService(config, anonymizerService);
			jdbcService.anonymize();

		} else if (config.getFiles() != null && !config.getFiles().isEmpty()) {
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

	private static void printDemoConfiguration() throws Exception {
		System.out.println("\nSupported Database URL formats:");
		JdbcAnonymizerService s = new JdbcAnonymizerService();

		Map<String, String> supportedJdbc = s.getSupportedDriverURLs();
		int col1width = getTextWidth(supportedJdbc.keySet());

		String twoColumnFormat = "%-" + col1width + "s %s";
		System.out.println(String.format(twoColumnFormat, "Jdbc URL format", "By Driver"));

		for (Entry<String, String> entry : supportedJdbc.entrySet()) {
			System.out.println(String.format(twoColumnFormat, entry.getKey(), entry.getValue()));
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
