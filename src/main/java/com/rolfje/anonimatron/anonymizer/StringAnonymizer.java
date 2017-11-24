package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class StringAnonymizer implements Anonymizer {
	private static final String TYPE = "STRING";

	@Override
	public Synonym anonymize(Object from, int size) {
		StringSynonym s = new StringSynonym();
		s.setType(TYPE);
		s.setFrom(from);

		if (from == null) {
			s.setTo(null);
		} else if (from instanceof String) {
			String randomHexString = Long.toHexString(Double.doubleToLongBits(Math.random()));
			
			if (randomHexString.length() > size){
				throw new UnsupportedOperationException("Can not generate a random hex string with length "+size
					+". Generated String size is "+randomHexString.length()+" characters.");
			}
			
			s.setTo(randomHexString);
		} else {
			throw new UnsupportedOperationException("Can not anonymize objects of type " + from.getClass());
		}

		return s;
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
