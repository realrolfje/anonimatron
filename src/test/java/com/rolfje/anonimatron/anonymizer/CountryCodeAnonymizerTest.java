package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;

import java.util.*;

import static org.junit.Assert.assertNotEquals;

public class CountryCodeAnonymizerTest extends TestCase {

    private static final Set<String> ISO_3_COUNTRY_CODES = getISO3CountryCodes();

    public void testAnonymize() {

        testInternal(2, "EN");
        testInternal(3, "NLD");
        testInternal(4, "BLR");
    }

    private void testInternal(int size, String from) {
        CountryCodeAnonymizer countryCodeAnonymizer = new CountryCodeAnonymizer();
        Synonym nld = countryCodeAnonymizer.anonymize(from, size, false);
        assertEquals(countryCodeAnonymizer.getType(), nld.getType());
        assertEquals(from, nld.getFrom());
        assertNotEquals(from, nld.getTo());
        assertEquals("String length check failed, should be " + size + ". From: " + nld.getFrom() + " To: " + nld.getTo(),
                size, ((String) nld.getTo()).length());
        assertFalse(nld.isShortLived());
    }

    public void testThreeDigitCountryCodes_shouldAnonymizeToThreeDigitCountryCodes() {
        CountryCodeAnonymizer anonymizer = new CountryCodeAnonymizer();
        String countryCode = "NLD";
        for (int i = 0; i < 100; i++) {
            Synonym synonym = anonymizer.anonymize(countryCode, 3, false);
            assertTrue(synonym.getTo() + " is not a valid country code.",
                    ISO_3_COUNTRY_CODES.contains(String.valueOf(synonym.getTo())));
        }
    }

    private static Set<String> getISO3CountryCodes() {
        Locale[] availableLocales = Locale.getAvailableLocales();
        Set<String> iso3CountryCodes = new HashSet<>(availableLocales.length);
        for (Locale locale : availableLocales) {
            try {
                iso3CountryCodes.add(locale.getISO3Country());
            } catch (MissingResourceException e) {
                // don't add
            }
        }
        iso3CountryCodes.remove("");
        return iso3CountryCodes;
    }
}
