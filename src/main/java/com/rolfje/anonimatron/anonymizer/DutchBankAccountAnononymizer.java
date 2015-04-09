package com.rolfje.anonimatron.anonymizer;

import org.apache.log4j.Logger;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

/**
 * Generates valid Dutch Bank Account number with the same length as the given
 * number.
 * <p>
 * A Dutch Bank Account Number is passes the "11 proof" if it is 9 digits long.
 * If it is less than 9 digits, there is no way to check the validity of the
 * number.
 * <p>
 * See http://nl.wikipedia.org/wiki/Elfproef
 */
public class DutchBankAccountAnononymizer extends AbstractElevenProofAnonymizer {
	private static Logger LOG = Logger.getLogger(DutchBankAccountAnononymizer.class);

	private static int LENGTH = 9;

	@Override
	public String getType() {
		return "DUTCHBANKACCOUNT";
	}

	@Override
	public Synonym anonymize(Object from, int size) {
		if (size < LENGTH) {
			throw new UnsupportedOperationException(
					"Can not generate a Dutch Bank Account number that fits in a "
							+ size
							+ " character string. Must be " + LENGTH + " characters or more.");
		}

		int originalLength = ((String)from).length();
		if (originalLength > LENGTH) {
			LOG.warn("Original bank account number had more than " + LENGTH
					+ " digits. The resulting anonymous bank account number with the same length will not be a valid account number.");

		}

		StringSynonym s = new StringSynonym();
		s.setType(getType());
		s.setFrom(from);

		do {
			// Never generate identical number
			s.setTo(generateBankAccount(originalLength));
		} while (s.getFrom().equals(s.getTo()));

		return s;
	}

	String generateBankAccount(int numberOfDigits) {
		if (numberOfDigits == LENGTH) {
			// Generate 11-proof bank account number
			int[] elevenProof = generate11ProofNumber(numberOfDigits);
			return digitsAsNumber(elevenProof);
		} else {
			// Generate non-11 proof bank account number
			int[] randomnumber = getRandomDigits(numberOfDigits);
			return digitsAsNumber(randomnumber);
		}
	}
}
