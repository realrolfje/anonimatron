package com.rolfje.anonimatron.anonymizer;

import java.util.HashMap;
import java.util.Map;

import com.rolfje.anonimatron.synonyms.Synonym;

/**
 * Provides functionality for consistently anonymizing a piece of data with provided parameters.
 * @see com.rolfje.anonimatron.anonymizer.Anonymizer
 */
public interface ParameterizedAnonymizer extends Anonymizer {

    /**
     * Anonymizes the given data into a non-tracable, non-reversible synonym
     * with the provided parameters, and does it consistently, so that A
     * always translates to B.
     * @param from       the data to be anonymized, usually passed in as a
     *                   {@link String}, {@link Integer}, {@link java.sql.Date} or other classes
     *                   which can be stored in a single JDBC database column.
     * @param size       the optional maximum size of the generated value
     * @param shortlived indicates that the generated synonym must have the
     *                   {@link Synonym#isShortLived()} boolean set
     * @param parameters the parameters to be used by the anonymizer
     * @return a {@link Synonym}
     */
    Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters);

    @Override
    default Synonym anonymize(Object from, int size, boolean shortlived) {
        return anonymize(from, size, shortlived, new HashMap<>());
    }

}
