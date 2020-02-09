package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Test;

import static org.junit.Assert.*;

public class DigitStringAnonymizerTest {

    @Test
    public void anonymize() {
        DigitStringAnonymizer anonymizer = new DigitStringAnonymizer();
        String original = "ORIGINAL";

        Synonym synonym = anonymizer.anonymize(original, Integer.MAX_VALUE, false);

        assertNotNull(synonym);
        assertNotEquals(synonym.getFrom(), synonym.getTo());
        assertEquals(anonymizer.getType(), synonym.getType());
        assertEquals(original.length(), synonym.getTo().toString().length());
        assertFalse(synonym.isShortLived());
    }
}