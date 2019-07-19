package com.rolfje.anonimatron.anonymizer;

import java.util.Map;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class FixedValueAnonymizer implements ParameterizedAnonymizer {
    private static final String TYPE = "FIXED";

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
        if (parameters == null || !parameters.containsKey("value")) {
            throw new UnsupportedOperationException("no value");
        }
        return new StringSynonym(getType(),
                (String) from,
                parameters.get("value"),
                shortlived);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
