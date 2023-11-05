package com.rolfje.anonimatron.commandline;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import static com.rolfje.anonimatron.Anonimatron.VERSION;

public class CommandLine {

    private static final String OPT_JDBCURL = "jdbcurl";
    private static final String OPT_USERID = "userid";
    private static final String OPT_PASSWORD = "password";
    private static final String OPT_CONFIGFILE = "config";
    private static final String OPT_SYNONYMFILE = "synonyms";
    private static final String OPT_DRYRUN = "dryrun";
    private static final String OPT_CONFIGEXAMPLE = "configexample";

    private static final Options options = new Options()
            .addOption(OPT_CONFIGFILE, true,
                    "The XML Configuration file describing what to anonymize.")
            .addOption(OPT_SYNONYMFILE, true,
                    "The XML file to read/write synonyms to. "
                            + "If the file does not exist it will be created.")
            .addOption(OPT_CONFIGEXAMPLE, false,
                    "Prints out a demo/template configuration file.")
            .addOption(OPT_DRYRUN, false, "Do not make changes to the database.")
            .addOption(OPT_JDBCURL, true,
                    "The JDBC URL to connect to. " +
                            "If provided, overrides the value in the config file.")
            .addOption(OPT_USERID, true,
                    "The user id for the database connection. " +
                            "If provided, overrides the value in the config file.")
            .addOption(OPT_PASSWORD, true,
                    "The password for the database connection. " +
                            "If provided, overrides the value in the config file.");

    private final org.apache.commons.cli.CommandLine clicommandLine;

    public CommandLine(String[] arguments) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        clicommandLine = parser.parse(options, arguments);
    }

    public static void printHelp() {
        System.out
                .println("\nThis is Anonimatron " + VERSION + ", a command line tool to consistently \n"
                        + "replace live data in your database or data files with data which \n"
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

    public String getJdbcurl() {
        return clicommandLine.getOptionValue(CommandLine.OPT_JDBCURL);
    }

    public String getUserid() {
        return clicommandLine.getOptionValue(CommandLine.OPT_USERID);
    }

    public String getPassword() {
        return clicommandLine.getOptionValue(CommandLine.OPT_PASSWORD);
    }

    public String getConfigfileName() {
        return clicommandLine.getOptionValue(CommandLine.OPT_CONFIGFILE);
    }

    public String getSynonymfileName() {
        return clicommandLine.getOptionValue(CommandLine.OPT_SYNONYMFILE);
    }

    public boolean isDryrun() {
        return clicommandLine.hasOption(CommandLine.OPT_DRYRUN);
    }

    public boolean isConfigExample() {
        return clicommandLine.hasOption(CommandLine.OPT_CONFIGEXAMPLE);
    }
}
