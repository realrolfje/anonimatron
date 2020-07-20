package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Tests for {@link DutchZipCodeAnonymizer}.
 *
 * @author Erik-Berndt Scheper
 */
public class DutchZipCodeAnonymizerTest {

    private DutchZipCodeAnonymizer anonymizer = new DutchZipCodeAnonymizer();
    private Pattern pattern = Pattern.compile("[1-9][0-9]{3} ?(?!SA|SD|SS)[A-Z]{2}$");

    @Test
    public void anonymize() {
        for (int i = 0; i < 1000000; i++) {
            String from = anonymizer.buildZipCode();
            assertTrue(isValidZipCode(from));
            assertEquals(6, from.length());

            internalAnonymize(6, from);
        }
    }

    private void internalAnonymize(int size, String from) {
        Synonym synonym = anonymizer.anonymize(from, size, false);
        assertEquals(anonymizer.getType(), synonym.getType());

        String value = (String) synonym.getTo();
        assertTrue(isValidZipCode(value));
        assertFalse(synonym.isShortLived());
    }

    private boolean isValidZipCode(String value) {
        return pattern.matcher(value).matches();
    }

}