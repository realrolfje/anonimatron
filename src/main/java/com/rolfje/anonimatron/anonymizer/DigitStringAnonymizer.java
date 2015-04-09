package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class DigitStringAnonymizer extends AbstractElevenProofAnonymizer {

	@Override
	public String getType() {
		return "RANDOMDIGITS";
	}

	@Override
	public Synonym anonymize(Object from, int size) {
		int length = from.toString().length();

		int[] digits = getRandomDigits(length);
		String to = digitsAsNumber(digits);

		StringSynonym stringSynonym = new StringSynonym();
		stringSynonym.setFrom(from);
		stringSynonym.setType(getType());
		stringSynonym.setTo(to);

		return stringSynonym;
	}

}
