package com.rolfje.anonimatron.anonymizer;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RomanNameGeneratorTest extends TestCase {
    Logger LOG = LogManager.getLogger(RomanNameGenerator.class);

    public void testUniqueness() throws Exception {
        RomanNameGenerator r = new RomanNameGenerator();
        Set<String> names = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            String name = (String) r.anonymize("fakename", 100, false).getTo();
            assertFalse("The name " + name
                            + " was already generated. This is iteration " + i,
                    names.contains(name));
            names.add(name);
        }
    }

    public void testNullFrom() throws Exception {
        RomanNameGenerator r = new RomanNameGenerator();
        assertNull(r.anonymize(null, 100, false).getTo());
    }

    // commented out for speed reasons
    public void xxxtestMaxNames() throws Exception {

        RomanNameGenerator r = new RomanNameGenerator();
        Set<String> names = new HashSet<>();

        try {
            String name;
            boolean wasInSet = false;
            do {
                name = (String) r.anonymize("fakename", 100, false).getTo();
                wasInSet = names.contains(name);
                names.add(name);
            } while (!wasInSet);
        } catch (UnsupportedOperationException e) {
            // expected, generator crashes when it runs out of names.
        }

        LOG.info("RomanNameGenerator can generate " + names.size()
                + " unique names.");
    }
}
