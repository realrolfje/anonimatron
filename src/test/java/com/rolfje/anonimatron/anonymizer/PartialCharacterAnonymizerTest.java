package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;

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
        String characters = "ABCDEFGHIJKLMNOPRSTUWVXYZ";
        partialCharacterAnonymizer.NUMBER_OF_SKIPPED_CHARACTERS = 5;

        Map<String, String> m = new HashMap<>();
        m.put(PartialCharacterAnonymizer.PARAMETER, characters);
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 5, false, m);

        String to = (String) synonym.getTo();

        System.out.println(from);
        System.out.println(to);
    }

    public void testShouldAnonymizeCharactersAfterSkippingCharactersUsingDigitals(){

        String from = "3551085";
        String digitals = "1234567890";
        partialCharacterAnonymizer.NUMBER_OF_SKIPPED_CHARACTERS = 3;

        Map<String, String> m = new HashMap<>();
        m.put(PartialCharacterAnonymizer.PARAMETER, digitals);
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 5, false, m);

        String to = (String) synonym.getTo();

        System.out.println(from);
        System.out.println(to);
    }
}
