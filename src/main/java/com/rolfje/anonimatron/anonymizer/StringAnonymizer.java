package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class StringAnonymizer implements Anonymizer {
    private static final String TYPE = "STRING";

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        String randomHexString = getString(from, size);

        return new StringSynonym(
                getType(),
                (String) from,
                randomHexString,
                shortlived
        );
    }

    private String getString(Object from, int size) {
        if (from == null) {
            return null;
        }

        if (from instanceof String) {
            String randomHexString = Long.toHexString(Double.doubleToLongBits(Math.random()));

            if (randomHexString.length() > size) {
                throw new UnsupportedOperationException("Can not generate a random hex string with length " + size
                        + ". Generated String size is " + randomHexString.length() + " characters.");
            }
            return randomHexString;
        }

        throw new UnsupportedOperationException("Can not anonymize objects of type " + from.getClass());
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
