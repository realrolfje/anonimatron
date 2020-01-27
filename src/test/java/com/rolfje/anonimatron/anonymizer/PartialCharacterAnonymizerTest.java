package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for {@link PartialCharacterAnonymizer}.
 *
 * @author Bartłomiej Komendarczuk
 */

public class PartialCharacterAnonymizerTest extends TestCase {

    private PartialCharacterAnonymizer partialCharacterAnonymizer = new PartialCharacterAnonymizer();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        partialCharacterAnonymizer = new PartialCharacterAnonymizer();
    }

    public void testShouldAnonymizeCharactersAfterSkippingCharacters(){

        String from = "TEST_ANONYMIZER";
        String CHARS = "ABCDEFGHIJKLMNOPRSTUWVXYZ";
        partialCharacterAnonymizer.NUMBER_OF_SKIPPED_CHARACTERS = 5;

        Map<String, String> m = new HashMap<>();
        m.put(PartialCharacterAnonymizer.PARAMETER, CHARS);
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 5, false, m);

        String to = (String) synonym.getTo();

        Assert.assertNotSame(to.substring(5) +" is not different than " + from.substring(5), to.substring(5) ,from.substring(5));
    }

    public void testShouldAnonymizeCharactersAfterSkippingCharactersUsingDigitals(){

        String from = "3551085";
        String CHARS = "1234567890";
        partialCharacterAnonymizer.NUMBER_OF_SKIPPED_CHARACTERS = 3;

        Map<String, String> m = new HashMap<>();
        m.put(PartialCharacterAnonymizer.PARAMETER, CHARS);
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 5, false, m);

        String to = (String) synonym.getTo();

        Assert.assertNotSame(to.substring(3) +" is not different than " + from.substring(3), to.substring(3) ,from.substring(3));
    }
}
