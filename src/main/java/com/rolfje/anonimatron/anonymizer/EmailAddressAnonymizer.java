package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class EmailAddressAnonymizer implements Anonymizer {
	private static final String EMAIL_DOMAIN = "@example.com";
	private static final String TYPE = "EMAIL_ADDRESS";

	@Override
	public Synonym anonymize(Object from, int size, boolean shortlived) {
		String randomHexString = null;
		if (from instanceof String) {
			randomHexString = Long.toHexString(Double
					.doubleToLongBits(Math.random()));
			randomHexString += EMAIL_DOMAIN;

			if (randomHexString.length() > size) {
				throw new UnsupportedOperationException(
						"Can not generate email address with length " + size
								+ ".");
			}

		} else {
			throw new UnsupportedOperationException(
					"Can not anonymize objects of type " + from.getClass());
		}

		return new StringSynonym(
				getType(),
				(String) from,
				randomHexString,
				shortlived
		);
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
