package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.concurrent.ThreadLocalRandom;

public class IPAddressV4Anonymizer implements Anonymizer {
    private static final String TYPE = "IP_ADDRESS_V4";

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

    private int ipChunk() {
        return ThreadLocalRandom.current().nextInt(0, 256);
    }

    private String getString(Object from, int size) {
        if (from == null) {
            return null;
        }

        if (from instanceof String) {
            String to = String.format("127.%d.%d.%d", ipChunk(), ipChunk(), ipChunk());

            if (to.length() > size) {
                throw new UnsupportedOperationException(
                        "Can not reliably generate a version 4 IP address smaller than " + size + " characters.");
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
