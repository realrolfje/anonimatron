package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

/**
 * Generates valid Dutch "Burger Service Nummer" or "SOFI Nummer",a social
 * security number uniquely identifying civilians to the governement.
 * <p>
 * A BSN Number is 9 digits long, can not start with 3 zeroes, the first digit
 * must be less than 4, and the whole number passes the "11 proof"
 * <p>
 * See http://nl.wikipedia.org/wiki/Rekeningnummer
 */
public class DutchBSNAnononymizer extends AbstractElevenProofAnonymizer {
	private static int LENGTH = 9;

	@Override
	public String getType() {
		return "BURGERSERVICENUMMER";
	}

	@Override
	public Synonym anonymize(Object from, int size) {
		if (size < LENGTH) {
			throw new UnsupportedOperationException(
					"Can not generate a BSN that fits in a "
							+ size
							+ " character string. Must be " + LENGTH + " characters or more.");
		}

		StringSynonym s = new StringSynonym();
		s.setType(getType());
		s.setFrom(from);

		do {
			// Never generate identical number
			s.setTo(generateBSN(LENGTH));
		} while (s.getFrom().equals(s.getTo()));

		return s;
	}

	String generateBSN(int numberOfDigits) {
		// Generate random BSN number
		int[] bsnnumber;

		do {
			bsnnumber = generate11ProofNumber(numberOfDigits);

			// SOFI numbers can not start with 3 zeroes, left digit digit van
			// not be >3
		} while ((bsnnumber[0] > 3) && (0 != (bsnnumber[0] + bsnnumber[1] + bsnnumber[2])));

		// Return the BSN
		String result = digitsAsNumber(bsnnumber);
		return result;
	}
}
