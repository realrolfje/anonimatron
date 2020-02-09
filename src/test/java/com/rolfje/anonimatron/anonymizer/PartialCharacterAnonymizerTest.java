package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for {@link PartialDigitsAnonymizer}.
 *
 * @author Bartłomiej Komendarczuk
 */

public class PartialCharacterAnonymizerTest extends TestCase {

    private PartialDigitsAnonymizer partialCharacterAnonymizer = new PartialDigitsAnonymizer();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        partialCharacterAnonymizer = new PartialDigitsAnonymizer();
    }

    public void testShouldAnonymizeCharactersAfterSkippingCharacters(){

        String from = "TEST_123";

        Map<String, String> m = new HashMap<>();
        m.put(PartialDigitsAnonymizer.PARAMETER, String.valueOf(3));
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 5, false, m);

        String to = (String) synonym.getTo();

        Assert.assertNotSame(to.substring(3) +" is not different than " + from.substring(3), to.substring(3) ,from.substring(3));
    }

    public void testShouldNotAnonymizeTheFirstCharacters(){

        String from = "TEST_ANONYMIZER";

        Map<String, String> m = new HashMap<>();
        m.put(PartialDigitsAnonymizer.PARAMETER, String.valueOf(5));
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 5, false, m);

        String to = (String) synonym.getTo();

        Assert.assertEquals(to.substring(0,5) + " is not same as " + from.substring(0,5),to.substring(0,5), from.substring(0,5));
    }

    public void testShouldAnonymizeCharactersAfterSkippingCharactersUsingDigitals(){

        String from = "3551085";

        Map<String, String> m = new HashMap<>();
        m.put(PartialDigitsAnonymizer.PARAMETER, String.valueOf(3));
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 3, false, m);

        String to = (String) synonym.getTo();

        Assert.assertNotSame(to.substring(3) +" is not different than " + from.substring(3), to.substring(3) ,from.substring(3));
    }

    public void testShouldNotAnonymizeTheFirstCharactersUsingDigitals(){

        String from = "3551085";

        Map<String, String> m = new HashMap<>();
        m.put(PartialDigitsAnonymizer.PARAMETER, String.valueOf(5));
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 5, false, m);

        String to = (String) synonym.getTo();

        Assert.assertEquals(to.substring(0,5) + " is not same as " + from.substring(0,5),to.substring(0,5), from.substring(0,5));
    }
}
