package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DigitStringAnonymizerTest {

    private DigitStringAnonymizer anonymizer;

    @Before
    public void setUp() throws Exception {
        anonymizer = new DigitStringAnonymizer();
    }

    @Test
    public void anonymize() {
        String original = "ORIGINAL";

        Synonym synonym = anonymizer.anonymize(original, Integer.MAX_VALUE, false);

        assertNotNull(synonym);
        assertNotEquals(synonym.getFrom(), synonym.getTo());
        assertEquals(anonymizer.getType(), synonym.getType());
        assertEquals(original.length(), synonym.getTo().toString().length());
        assertFalse(synonym.isShortLived());
    }

    @Test
    public void testMaskedAnonymize() {
        String original = "ABCDEFGH";
        String mask     = "1,1-1*99";
        String expected = "AxCxExGH";

        Synonym synonym = anonymizer.anonymize(
                original, Integer.MAX_VALUE, false,
                getMaskParameters(mask)
        );

        String toString = synonym.getTo().toString();

        assertEquals(original, synonym.getFrom());
        assertNotEquals(synonym.getFrom(), synonym.getTo());
        assertEquals(synonym.getFrom().toString().length(), toString.length());

        char[] toChars = toString.toCharArray();
        char[] expectedChars = expected.toCharArray();

        for (int i = 0; i < expectedChars.length; i++) {
            assertTrue("Character at position " + i + " not what we expected. String is '" + toString + "'",
                    (expectedChars[i] == 'x') == (Character.isDigit(toChars[i])));
        }
    }

    private Map<String, String> getMaskParameters(String mask) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(anonymizer.PARAMETER, mask);
        return parameters;
    }
}