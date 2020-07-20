package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import com.rolfje.junit.Asserts;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.rolfje.anonimatron.anonymizer.DutchBSNAnononymizer.isValidBSN;
import static org.junit.Assert.*;

public class DutchBSNAnononymizerTest {

    private DutchBSNAnononymizer bsnAnonymizer = new DutchBSNAnononymizer();

    private String original;
    boolean shortlived = true;


    @Before
    public void setUp() throws Exception {
        original = bsnAnonymizer.generateBSN(9);
    }

    @Test
    public void testAnonymizeNumbers() {
        Object[] originals = {
                Integer.valueOf(original),
                Long.valueOf(original),
                new BigDecimal(original),
                new Integer(original)
        };

        for (Object originalAsNumber : originals) {
            Synonym synonym = bsnAnonymizer.anonymize(originalAsNumber, 9, shortlived);
            assertEquals(originalAsNumber, synonym.getFrom());
            assertNotEquals(originalAsNumber, synonym.getTo());
            Class<?> expectedClass = originalAsNumber.getClass();
            Object actualClass = synonym.getTo();
            Asserts.assertInstanceOf(expectedClass, actualClass);
            assertEquals(shortlived, synonym.isShortLived());
            validate(synonym);
        }
    }


    @Test
    public void testLength() throws Exception {
        bsnAnonymizer.anonymize("dummy", 10000, false);
        bsnAnonymizer.anonymize("dummy", 9, false);

        try {
            bsnAnonymizer.anonymize("dummy", 8, false);
            fail("should throw exception");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    @Test
    public void testGenerateBSN() {
        boolean hadNonZeroLastDigit = false;
        for (int i = 0; i < 1000; i++) {
            String bsn = bsnAnonymizer.generateBSN(9);
            assertTrue("Incorrect BSN " + bsn, isValidBSN(bsn));
            hadNonZeroLastDigit = hadNonZeroLastDigit || !bsn.endsWith("0");
        }
        assertTrue("BSN ending in 0 can pass both BSN and regular 11 proof test.", hadNonZeroLastDigit);
    }

    @Test
    public void testKnownFailingBSNs() {
        String[] invalidBSNs = new String[]{"815098", "9815098", "920006450", "529790203", "223118818",};
        for (String invalidBSN : invalidBSNs) {
            assertFalse("Valid BSN reported as invalid: " + invalidBSN, isValidBSN(invalidBSN));
        }
    }

    @Test
    public void testKnownCorrectBSNs() {
        String[] validBSNs = new String[]{"111222333", "123456782"};
        for (String validBSN : validBSNs) {
            assertTrue("Correct BSN reported as incorrect: " + validBSN, isValidBSN(validBSN));
        }
    }

    private void validate(Synonym synonym) {
        assertNotEquals(synonym.getFrom(), synonym.getTo());
        assertNotNull(synonym.getType());
        String burgerServiceNummer = toString(synonym.getTo());
        assertTrue(isValidBSN(burgerServiceNummer));
    }

    private String toString(Object value) {
        String toString;
        if (value instanceof Integer) {
            toString = String.format("%09d", (Integer) value);
        } else if (value instanceof Long) {
            toString = String.format("%09d", (Long) value);
        } else if (value instanceof BigInteger) {
            toString = String.format("%09d", ((BigInteger) value).longValue());
        } else if (value instanceof BigDecimal) {
            toString = String.format("%09d", ((BigDecimal) value).longValue());
        } else {
            toString = value.toString();
        }
        return toString;
    }


}
