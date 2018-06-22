package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.UUID;

public class UUIDAnonymizer implements Anonymizer {
    private static final String TYPE = "UUID";

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        String to = null;
        if (from == null) {
            to = null;
        } else if (from instanceof String) {
            to = UUID.randomUUID().toString();

            if (to.length() > size) {
                throw new UnsupportedOperationException(
                        "Can not generate a UUID smaller than " + size
                                + " characters.");
            }

        } else {
            throw new UnsupportedOperationException(
                    "Can not anonymize objects of type " + from.getClass());
        }
        return new StringSynonym(
                getType(),
                (String) from,
                to,
                shortlived
        );
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
