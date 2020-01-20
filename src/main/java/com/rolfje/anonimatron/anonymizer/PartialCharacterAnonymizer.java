package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Map;
import java.util.Random;

public class PartialCharacterAnonymizer implements Anonymizer {

    public static String PARAMETER = "characters";

    protected String CHARS;

    public static int NUMBER_OF_SKIPPED_CHARACTERS;

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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            for (int k = NUMBER_OF_SKIPPED_CHARACTERS; k < length; k++) {
                sb.append(characters.charAt(r.nextInt(characters.length())));
            }
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
