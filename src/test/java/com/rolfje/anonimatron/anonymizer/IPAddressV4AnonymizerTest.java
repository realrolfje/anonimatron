package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressV4AnonymizerTest {

    private IPAddressV4Anonymizer ipAnonymizer;

    @Before
    public void setUp() {
        ipAnonymizer = new IPAddressV4Anonymizer();
    }

    @Test
    public void testHappyFlow() {
        Synonym synonym = ipAnonymizer.anonymize("192.168.1.1", Integer.MAX_VALUE, false);

        assertNotEquals(synonym.getFrom(), synonym.getTo());
        assertEquals(ipAnonymizer.getType(), synonym.getType());
        assertFalse(synonym.isShortLived());
        // Example from anonymizer is 127.243.0.15
        assertTrue(Pattern.matches("^127\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}$", synonym.getTo().toString()));
    }

    @Test
    public void testNullInput() {
        Synonym synonym = ipAnonymizer.anonymize(null, Integer.MAX_VALUE, true);
        assertNull(synonym.getFrom());
        assertNull(synonym.getTo());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIncorrectInputType() {
        ipAnonymizer.anonymize(new Long(0), Integer.MAX_VALUE, true);
    }
}