package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class CharacterStringPrefetchAnonymizerTest extends TestCase {

    CharacterStringPrefetchAnonymizer anonimyzer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        anonimyzer = new CharacterStringPrefetchAnonymizer();
    }

    public void testPrefetch() {
        String sourceData = "ABC";
        anonimyzer.prefetch(sourceData);

        String from = "TEST1";
        Synonym synonym = anonimyzer.anonymize(from, 5, false);

        String to = (String) synonym.getTo();
        assertEquals("from and to string lengths did not match", from.length(), to.length());

        for (int i = 0; i < to.length(); i++) {
            assertTrue("'to' string contained characters which were not in the source data."
                    , sourceData.indexOf(to.charAt(i)) > -1);
        }
    }

    public void testParameterizedCharacterString() {
        String from = "TEST1";
        String characters = "#$%";

        Map<String, String> m = new HashMap<>();
        m.put(CharacterStringAnonymizer.PARAMETER, characters);
        Synonym synonym = anonimyzer.anonymize(from, 5, false, m);

        String to = (String) synonym.getTo();
        for (int i = 0; i < to.length(); i++) {
            assertTrue("'to' string contained characters which were not in the parameter."
                    , characters.indexOf(to.charAt(i)) > -1);
        }
    }

    public void testWrongParameter() {
        try {
            anonimyzer.anonymize("any", 10, false, null);

            anonimyzer.anonymize("any", 10, false, new HashMap<>());

            anonimyzer.anonymize("any", 10, false, new HashMap<String, String>() {{
                put("PaRaMeTeR", "any");
            }});

            fail("Using the wrong parameters should throw an exception.");
        } catch (UnsupportedOperationException e) {
            assertTrue("Exception should point to the character parameter",
                    e.getMessage().contains(CharacterStringAnonymizer.PARAMETER));
        }
    }

    public void testPrefetchNull() {
        anonimyzer.prefetch(null);
        String from = "DUMMY";
        Synonym synonym = anonimyzer.anonymize(from, 5, false);

        String to = (String) synonym.getTo();
        assertEquals("from and to string lengths did not match", from.length(), to.length());
    }
}
