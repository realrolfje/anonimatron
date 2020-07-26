package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class StringAnonymizerTest {

    private StringAnonymizer stringAnonymizer;

    @Before
    public void setUp() {
        stringAnonymizer = new StringAnonymizer();
    }

    @Test
    public void testHappyFlow() {
        Synonym synonym = stringAnonymizer.anonymize("FROM", Integer.MAX_VALUE, false);

        assertNotEquals(synonym.getFrom(), synonym.getTo());
        assertEquals(stringAnonymizer.getType(), synonym.getType());
        assertFalse(synonym.isShortLived());
    }

    @Test
    public void testNullInput() {
        Synonym synonym = stringAnonymizer.anonymize(null, Integer.MAX_VALUE, true);
        assertNull(synonym.getFrom());
        assertNull(synonym.getTo());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIncorrectInputType() {
        stringAnonymizer.anonymize(new Long(0), Integer.MAX_VALUE, true);
    }
}