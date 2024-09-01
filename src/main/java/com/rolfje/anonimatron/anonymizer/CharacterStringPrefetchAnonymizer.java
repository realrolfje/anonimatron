package com.rolfje.anonimatron.anonymizer;


import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides the same functionality as {@link CharacterStringAnonymizer}, but
 * uses the prefetch cycle to collect its output character set. This causes
 * the anonymized dataset to contain the same characters as used the input set,
 * enabling debugging/reproduction of strange character set issues.
 *
 * @author rolf
 */
public class CharacterStringPrefetchAnonymizer extends CharacterStringAnonymizer implements Prefetcher {
    private static final Logger LOG = LogManager.getLogger(CharacterStringPrefetchAnonymizer.class);

    public CharacterStringPrefetchAnonymizer() {
        CHARS = "";
    }

    @Override
    public void prefetch(Object sourceData) {
        if (sourceData == null) {
            return;
        }

        char[] sourcechars = sourceData.toString().toCharArray();
        for (char c : sourcechars) {
            if (CHARS.indexOf(c) == -1) {
                CHARS += c;
            }
        }
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        if (CHARS.length() < 1) {
            LOG.warn("No characters were collected during prefetch. Using the default set '" + getDefaultCharacterString() + "'.");
            CHARS = getDefaultCharacterString();
        }
        return super.anonymize(from, size, shortlived);
    }

    @Override
    public String getType() {
        return "PREFETCHCHARACTERS";
    }
}
