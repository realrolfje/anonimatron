package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class UkPostCodeAnonymizerTest {

    private UkPostCodeAnonymizer ukPostCodeAnonymizer;

    @Before
    public void setUp() throws Exception {
        ukPostCodeAnonymizer = new UkPostCodeAnonymizer();
    }

    @Test
    public void testValidFormatOfGeneratedCodes() {
        int numberOfCodes = 10_000;
        long max_time_ms = 1000; // Be forgiving here; not all machines perform equally

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10_000; i++) {
            assertValidPostalCode(ukPostCodeAnonymizer.buildZipCode());
        }
        long stop = System.currentTimeMillis();
        long duration = stop - start;

        assertTrue("Generating " + numberOfCodes + " took " + duration + ".", duration < max_time_ms);
    }

    @Test
    public void testPostalCodeFlavor1() {
        assertValidPostalCode(ukPostCodeAnonymizer.buildZipCodeFlavor1());
    }

    @Test
    public void testPostalCodeFlavor2() {
        assertValidPostalCode(ukPostCodeAnonymizer.buildZipCodeFlavor2());
    }


    private void assertValidPostalCode(String s) {

        if (s.length() > 8) {
            fail("length longer than 8");
        }

        // See https://stackoverflow.com/a/164994
        String regex = "^([Gg][Ii][Rr] ?0[Aa]{2})|" +
                "((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))" +
                " ?[0-9][A-Za-z]{2})$";

        Pattern regexPattern = Pattern.compile(regex);
        assertTrue("Invalid postal code generated: '" + s + "'", regexPattern.matcher(s).matches());
    }

    @Test
    public void testGenerateCorrectSynonym() {
        Synonym s1 = ukPostCodeAnonymizer.anonymize("x", 10, false);
        assertEquals(ukPostCodeAnonymizer.getType(), s1.getType());
        assertNotEquals(s1.getFrom(), s1.getTo());
        assertEquals(false, s1.isShortLived());

        Synonym s2 = ukPostCodeAnonymizer.anonymize("x", 10, true);
        assertEquals(ukPostCodeAnonymizer.getType(), s2.getType());
        assertNotEquals(s2.getFrom(), s2.getTo());
        assertEquals(true, s2.isShortLived());

        assertNotEquals(s1.getTo(), s2.getTo());
    }

    @Test
    public void testGenerateErrorForShortCodes() {
        try {
            ukPostCodeAnonymizer.anonymize("x", 5, false);
            fail("Should throw a configuration error.");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("will not always fit"));
        }
    }
}
