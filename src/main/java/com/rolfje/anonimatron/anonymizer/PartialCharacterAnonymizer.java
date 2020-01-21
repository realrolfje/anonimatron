package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Map;
import java.util.Random;

/**
 * Generates an output string based on the configured characters
 * omitting the specified number of initial characters.
 *
 * @author Bartłomiej Komendarczuk
 */

public class PartialCharacterAnonymizer implements Anonymizer {

    public static String PARAMETER = "characters";

    protected String CHARS;

    public int NUMBER_OF_SKIPPED_CHARACTERS;

    public PartialCharacterAnonymizer() {
        CHARS = getDefaultCharacterString();
    }

    protected String getDefaultCharacterString() {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    }

    @Override
    public String getType() {
        return "PARTIALRANDOMCHARACTERS";
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
        if (parameters == null || !parameters.containsKey(PARAMETER)) {
            throw new UnsupportedOperationException("Please provide '" + PARAMETER + "' as configuration parameter.");
        }
        return anonymize(from, size, shortlived, parameters.get(PARAMETER));
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        return anonymize(from, size, shortlived, CHARS);
    }

    private Synonym anonymize(Object from, int size, boolean shortlived, String characters) {
        String fromString = from.toString();
        Random r = new Random();

        int length = fromString.length();
        StringBuilder sb = new StringBuilder(fromString.substring(0, NUMBER_OF_SKIPPED_CHARACTERS));
        for (int i = NUMBER_OF_SKIPPED_CHARACTERS; i < length; i++) {
                sb.append(characters.charAt(r.nextInt(characters.length())));
        }

        String to = sb.toString();

        StringSynonym stringSynonym = new StringSynonym(
                getType(),
                fromString,
                to,
                shortlived
        );

        return stringSynonym;
    }
}
