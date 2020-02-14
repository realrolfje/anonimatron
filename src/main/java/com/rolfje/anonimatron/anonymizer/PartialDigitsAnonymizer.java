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

public class PartialDigitsAnonymizer implements Anonymizer {
    private static final Random random = new Random();

    private static final String DIGITS = "0123456789";

    public static final String PARAMETER = "skip";

    public PartialDigitsAnonymizer() {
    }

    @Override
    public String getType() {
        return "PARTIALRANDOMDIGITS";
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
        if (parameters == null || !parameters.containsKey(PARAMETER)) {
            throw new UnsupportedOperationException("Please provide '" + PARAMETER + "' parameter to indicate the number of characters to skip.");
        }
        return anonymize(from, size, shortlived, Integer.valueOf(parameters.get(PARAMETER)));
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        return anonymize(from, size, shortlived, 0);
    }

    private Synonym anonymize(Object from, int size, boolean shortlived, int charactersToSkip) {
        String fromString = from.toString();
        int length = fromString.length();

        StringBuilder sb = new StringBuilder(fromString.substring(0, charactersToSkip));
        for (int i = charactersToSkip; i < length; i++) {
            sb.append(sb.append(DIGITS.charAt(random.nextInt(DIGITS.length()))));
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
