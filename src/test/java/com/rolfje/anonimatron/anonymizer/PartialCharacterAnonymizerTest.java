package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.rolfje.anonimatron.anonymizer.PartialCharacterAnonymizer.NUMBER_OF_SKIPPED_CHARACTERS;

public class PartialCharacterAnonymizerTest extends TestCase {

    private PartialCharacterAnonymizer partialCharacterAnonymizer = new PartialCharacterAnonymizer();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        partialCharacterAnonymizer = new PartialCharacterAnonymizer();
    }

    public void shouldAnonymizeCharactersAfterSkippingCharacters() {
        String from = "TEST1";
        String characters = "#$%";

        Map<String, String> m = new HashMap<>();
        m.put(PartialCharacterAnonymizer.PARAMETER, characters);
        Synonym synonym = partialCharacterAnonymizer.anonymize(from, 5, false, m);

        int length = from.length();
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < length; i++) {
//            for (int k = NUMBER_OF_SKIPPED_CHARACTERS; k < length; k++) {
//                sb.append(characters.charAt(r.nextInt(characters.length())));
//            }
//        }


//        String to = (String) synonym.getTo();
//        for (int i = 0; i < to.length(); i++) {
//            assertTrue("'to' string contained characters which were not in the parameter."
//                    , characters.indexOf(to.charAt(i)) > -1);
//        }
    }
}
