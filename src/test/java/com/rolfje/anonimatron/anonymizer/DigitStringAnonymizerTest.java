package com.rolfje.anonimatron.anonymizer;

import com.javamonitor.tools.Stopwatch;
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
    public void testNull() {
        Synonym synonym = anonymizer.anonymize(null, Integer.MAX_VALUE, false);
        assertNull(synonym.getFrom());
        assertNull(synonym.getTo());
    }

    @Test
    public void testNullMasked() {
        Synonym synonym =
                anonymizer.anonymize(null, Integer.MAX_VALUE,
                        false, getMaskParameters("11**"));

        assertNull(synonym.getFrom());
        assertNull(synonym.getTo());
    }

    @Test
    public void testMaskedAnonymize() {
        String original = "ABCDEFGH";
        String mask = "1,1-1*99";
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

    @Test
    public void testMaskedDigitsPerformance() {
        String original = "ABCDEFGH";
        String mask = "1,1-1*99";
        Map<String, String> maskParameters = getMaskParameters(mask);

        Stopwatch stopwatchNano = new Stopwatch("Masked performance");
        for (int i = 0; i < 1_000_000; i++) {
            Synonym synonym = anonymizer.anonymize(
                    original, Integer.MAX_VALUE, false,
                    maskParameters
            );
        }

        // On a 2013 MacBook this takes less than 200 ms.
        // On the Travis build server this takes roughly 750 ms.
        boolean fastEnough = stopwatchNano.stop(1000);
        assertTrue(stopwatchNano.getMessage(), fastEnough);
    }

    private Map<String, String> getMaskParameters(String mask) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(anonymizer.PARAMETER, mask);
        return parameters;
    }

    @Test
    public void testIncorrectParamter() {
        try {
            anonymizer.anonymize("dummy", 0, false, new HashMap<String, String>() {{
                put("PaRaMeTeR", "any");
            }});
            fail("Should fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            assertEquals(
                    "Please provide '" + DigitStringAnonymizer.PARAMETER + "' with a digit mask in the form 111******, where only stars are replaced with random characters.",
                    e.getMessage());
        }
    }

    @Test
    public void testCorrectParameter() {
        Synonym anonymize = anonymizer.anonymize("dummy", 0, false, new HashMap<String, String>() {{
            put(DigitStringAnonymizer.PARAMETER, "any");
        }});

        // Actual anonymization tests done elsewhere
        assertNotNull(anonymize);
    }

}