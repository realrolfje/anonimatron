package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Map;

public class DigitStringAnonymizer extends AbstractElevenProofAnonymizer {

	final static String PARAMETER = "mask";

	@Override
	public String getType() {
		return "RANDOMDIGITS";
	}

	@Override
	public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
		if (parameters == null || !parameters.containsKey(PARAMETER)) {
			throw new UnsupportedOperationException("Please provide '" + PARAMETER + "' with a digit mask in the form 111******, where only stars are replaced with random characters.");
		}
		return anonymizeMasked(from, size, shortlived, parameters.get(PARAMETER));
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

	/**
	 * Anonymizes a string with digits into a new string of digits where only a part
	 * of the original digits are changed. You can use this to mask out a certain part
	 * of the original string, like an area code of a phone number, or the bank number
	 * of a credit card number.
	 *
	 * A mask looks like "111***", where digits remain original, and stars (or other
	 * characters) are replaced with random digits. An example:
	 *
	 * Original number: 555-1234
	 * Mask:            1111**** (note that the '-' is also untouched by placing a digit there)
	 * New number     : 555-9876
	 *
	 * Note that if the mask is shorter than the input string, the output string will
	 * have random digits where there is no mask. In short: only the locations where
	 * there is a digit in the mask are left untouched and will be in the output.
	 *
	 * @param from
	 * @param size
	 * @param shortlived
	 * @param mask
	 * @return
	 */
	private Synonym anonymizeMasked(Object from, int size, boolean shortlived, String mask) {
		Synonym anonymize = anonymize(from, size, shortlived);

		char[] fromChars = anonymize.getFrom().toString().toCharArray();
		char[] toChars = anonymize.getTo().toString().toCharArray();

		int length = Math.min(Math.min(mask.length(), fromChars.length), toChars.length);
		for (int i = 0; i < length; i++) {
			if (Character.isDigit(mask.charAt(i))) {
				// Not masked, put original digit in output
				toChars[i] = fromChars[i];
			}
		}

		return new StringSynonym(
				getType(),
				String.valueOf(fromChars),
				String.valueOf(toChars),
				shortlived
		);
	}
}
