package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class CharacterStringAnonymizerTest {

    CharacterStringAnonymizer characterStrAnon = new CharacterStringAnonymizer();

    @Test
    public void testIncorrectParamter() {
        try {
            characterStrAnon.anonymize("dummy", 0, false, new HashMap<String, String>() {{
                put("PaRaMeTeR", "any");
            }});
            fail("Should fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            assertEquals(
                    "Please provide '" + CharacterStringAnonymizer.PARAMETER + "' as configuration parameter.",
                    e.getMessage());
        }
    }

    @Test
    public void testCorrectParameter() {
        Synonym anonymize = characterStrAnon.anonymize("dummy", 0, false, new HashMap<String, String>() {{
            put(CharacterStringAnonymizer.PARAMETER, "any");
        }});

        // Actual anonymization tests done elsewhere
        assertNotNull(anonymize);
    }
}