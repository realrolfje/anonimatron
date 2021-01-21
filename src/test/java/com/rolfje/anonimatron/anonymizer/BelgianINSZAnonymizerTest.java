package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import com.rolfje.junit.Asserts;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.rolfje.anonimatron.anonymizer.BelgianINSZAnononymizer.isValidRRN;
import static com.rolfje.anonimatron.anonymizer.DutchBSNAnononymizer.isValidBSN;
import static org.junit.Assert.*;

public class BelgianINSZAnonymizerTest {

    private BelgianINSZAnononymizer RRNAnonymizer = new BelgianINSZAnononymizer();

    private String original;
    boolean shortlived = true;
    private String boomer = "64020505586";
    private String niller = "03072102865";

    @Before
    public void setUp() {
        original = RRNAnonymizer.generateRRN();
    }

    @Test
    public void testAnonymizeNumbers() {
        Object[] originals = {
                Long.valueOf(original),
                new BigDecimal(original)
        };

        for (Object originalAsNumber : originals) {
            Synonym synonym = RRNAnonymizer.anonymize(originalAsNumber, 11, shortlived);
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
    public void validValidate() {
        assertTrue(isValidRRN(boomer));
        assertTrue(isValidRRN(niller));
    }

    @Test
    public void testLength() {
        RRNAnonymizer.anonymize("123456789", 10000, false);

        try {
            RRNAnonymizer.anonymize("123456789", 9, false);
            fail("should throw exception");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    private void validate(Synonym synonym) {
        assertNotEquals(synonym.getFrom(), synonym.getTo());
        assertNotNull(synonym.getType());
        String rijksRegisterNummer = toString(synonym.getTo());
        assertTrue(isValidRRN(rijksRegisterNummer));
    }

    private String toString(Object value) {
        String toString;
        if (value instanceof Integer) {
            toString = String.format("%011d", (Integer) value);
        } else if (value instanceof Long) {
            toString = String.format("%011d", (Long) value);
        } else if (value instanceof BigInteger) {
            toString = String.format("%011d", ((BigInteger) value).longValue());
        } else if (value instanceof BigDecimal) {
            toString = String.format("%011d", ((BigDecimal) value).longValue());
        } else {
            toString = value.toString();
        }
        return toString;
    }
}
