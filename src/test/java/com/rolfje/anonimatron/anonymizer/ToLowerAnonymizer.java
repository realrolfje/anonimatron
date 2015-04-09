package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class ToLowerAnonymizer implements Anonymizer {

	@Override
	public String getType() {
		return "TO_LOWER_CASE";
	}

	@Override
	public Synonym anonymize(Object from, int size) {
		StringSynonym s = new StringSynonym();
		s.setFrom(from);
		s.setTo(((String)from).toLowerCase());
		return s;
	}
}
