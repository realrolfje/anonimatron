package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class DigitStringAnonymizer extends AbstractElevenProofAnonymizer {

	@Override
	public String getType() {
		return "RANDOMDIGITS";
	}

	@Override
	public Synonym anonymize(Object from, int size, boolean shortlived) {
		int length = from.toString().length();

		int[] digits = getRandomDigits(length);
		String to = digitsAsString(digits);

		StringSynonym stringSynonym = new StringSynonym(
				getType(),
				(String) from,
				to,
				shortlived
		);

		return stringSynonym;
	}

}
