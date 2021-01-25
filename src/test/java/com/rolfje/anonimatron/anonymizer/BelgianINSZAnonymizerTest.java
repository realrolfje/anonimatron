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
    
    private String list[] = {
            "68040626838",
            "66022431574",
            "68040404134",
            "68042803794",
            "61082062911",
            "93082928974",
            "77012728346",
            "98052925892",
            "67111632302",
            "80121603882",
            "70013039486",
            "72111054664",
            "67101003476",
             "78072331897",
             "80010568180",
             "71011845122",
            "68100651923",
            "79011828466",
            "89123121532",
            "77101922618",
            "71042616193",
            "69082927767",
            "74060114167",
            "77060516385",
            "66082837137",
            "88101245338",
            "90000012321",
            "00000010026"
    };

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
    public void testSampleList() {
        for (String rrn : list) {
            Synonym synonym = RRNAnonymizer.anonymize(rrn, 11, false);
            assertTrue(isValidRRN(rrn));
            assertTrue(isValidRRN((String) synonym.getTo()));
            System.out.println(rrn + " => " + (String) synonym.getTo());
        }
    }

    @Test
    public void testListKeepBirthDate() {
        for (String rrn : list) {
            Synonym synonym = RRNAnonymizer.anonymize(rrn, 11, false, true);
            assertTrue(isValidRRN((String) synonym.getTo()));
            System.out.println(rrn + " => " + (String) synonym.getTo());
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
