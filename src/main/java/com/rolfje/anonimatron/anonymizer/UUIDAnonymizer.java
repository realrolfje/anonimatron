package com.rolfje.anonimatron.anonymizer;

import java.util.UUID;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class UUIDAnonymizer implements Anonymizer {
	private static final String TYPE = "UUID";

	@Override
	public Synonym anonymize(Object from, int size) {
		StringSynonym s = new StringSynonym();
		s.setType(TYPE);
		s.setFrom(from);

		if (from == null) {
			s.setTo(null);
		} else if (from != null && from instanceof String) {
			String uuidString = UUID.randomUUID().toString();

			if (uuidString.length() > size) {
				throw new UnsupportedOperationException(
						"Can not generate a UUID smaller than " + size
								+ " characters.");
			}

			s.setTo(uuidString);
		} else {
			throw new UnsupportedOperationException(
					"Can not anonymize objects of type " + from.getClass());
		}

		return s;
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
