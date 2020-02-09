package com.rolfje.anonimatron.anonymizer;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class UkPostCodeAnonymizerTest {

    private UkPostCodeAnonymizer ukPostCodeAnonymizer;

    @Before
    public void setUp() throws Exception {
        ukPostCodeAnonymizer = new UkPostCodeAnonymizer();
    }

    @Test
    public void testValidFormatOfGeneratedCodes() {
        int numberOfCodes = 10_000;
        long max_time_ms = 500;

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10_000; i++) {
            String s = ukPostCodeAnonymizer.buildZipCode();
            System.out.println(s);
            assertValidPostalCode(s);
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
        // See https://stackoverflow.com/a/164994
        String regex = "^([Gg][Ii][Rr] ?0[Aa]{2})|" +
                "((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))" +
                " ?[0-9][A-Za-z]{2})$";

        Pattern regexPattern = Pattern.compile(regex);
        assertTrue("Invalid postal code generated: '" + s + "'", regexPattern.matcher(s).matches());
    }
}