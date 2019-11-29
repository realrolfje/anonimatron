package com.rolfje.anonimatron.commandline;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author schrader
 */
public class CommandLineTest {

    @Test
    public void parse_With_Options() throws Exception {
        String[] args = {
            "-config", "/Volumne/Data/config.xml",
            "-jdbcurl", "jdbc:postgresql://localhost:5433/",
            "-password", "lorem",
            "-userid", "ipsum",
            "-configexample",
            "-dryrun",
        };

        CommandLine cmdl = new CommandLine(args);
        assertThat(cmdl.getJdbcurl(), is("jdbc:postgresql://localhost:5433/"));
        assertThat(cmdl.getPassword(), is("lorem"));
        assertThat(cmdl.getUserid(), is("ipsum"));
        assertThat(cmdl.isConfigExample(), is(true));
        assertThat(cmdl.isDryrun(), is(true));
    }
}