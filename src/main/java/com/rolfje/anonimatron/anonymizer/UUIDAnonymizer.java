package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.UUID;

public class UUIDAnonymizer implements Anonymizer {
    private static final String TYPE = "UUID";

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        String to = getString(from, size);
        return new StringSynonym(
                getType(),
                (String) from,
                to,
                shortlived
        );
    }

    private String getString(Object from, int size) {
        if (from == null) {
            return null;
        }

        if (from instanceof String) {
            String to = UUID.randomUUID().toString();

            if (to.length() > size) {
                throw new UnsupportedOperationException(
                        "Can not generate a UUID smaller than " + size + " characters.");
            }
            return to;

        }

        throw new UnsupportedOperationException("Can not anonymize objects of type " + from.getClass());
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
