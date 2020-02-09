package com.rolfje.anonimatron.commandline;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals("jdbc:postgresql://localhost:5433/", cmdl.getJdbcurl());
        assertEquals("lorem", cmdl.getPassword());
        assertEquals("ipsum", cmdl.getUserid());
        assertTrue(cmdl.isConfigExample());
        assertTrue(cmdl.isDryrun());
    }
}