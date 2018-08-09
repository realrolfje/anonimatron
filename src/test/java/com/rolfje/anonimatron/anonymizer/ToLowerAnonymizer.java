package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class ToLowerAnonymizer implements Anonymizer {

    @Override
    public String getType() {
        return "TO_LOWER_CASE";
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        return new StringSynonym(
                getType(),
                (String) from,
                ((String) from).toLowerCase(),
                shortlived
        );
    }
}
