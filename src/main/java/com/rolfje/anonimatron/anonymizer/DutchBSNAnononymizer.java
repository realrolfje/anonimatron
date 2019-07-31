package com.rolfje.anonimatron.anonymizer;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.rolfje.anonimatron.synonyms.NumberSynonym;
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
					"Cannot generate a BSN that fits in a " + size + " character string. Must be " + LENGTH
							+ " characters or more.");
		}

		Synonym result;

		if (from instanceof Number) {
			result = new NumberSynonym();
			((NumberSynonym) result).setFrom((Number) from);
			((NumberSynonym) result).setType(getType());

		} else if (from instanceof String) {
			result = new StringSynonym();
			((StringSynonym) result).setFrom(from);
			((StringSynonym) result).setType(getType());

		} else {
			throw new IllegalArgumentException(
					"Type " + from.getClass().getSimpleName() + " is not supported for " + this.getType());

		}

		String value;
		String originalValue = from.toString();

		do {
			// Never generate identical number
			value = generateBSN(LENGTH);
		} while (originalValue.equals(value));

		if (result instanceof NumberSynonym) {
			((NumberSynonym) result).setTo(asNumber(value, (Number) from));

		} else {
			((StringSynonym) result).setTo(value);

		}

		return result;
	}

	private Number asNumber(String value, Number from) {
		if (from instanceof Integer) {
			return Integer.parseInt(value);
		} else if (from instanceof Long) {
			return Long.parseLong(value);
		} else if (from instanceof BigDecimal) {
			return new BigDecimal(value);
		} else if (from instanceof BigInteger) {
			return new BigInteger(value);
		} else {
			throw new IllegalArgumentException(from.getClass().getSimpleName() + " is not supported for " + this.getType());
		}
	}

	String generateBSN(int numberOfDigits) {
		// Generate random BSN number
		int[] bsnnumber;

		do {
			bsnnumber = generate11ProofNumber(numberOfDigits);

			// BSN cannot start with 3 zeroes, left digit digit cannot be > 3
		} while ((bsnnumber[0] > 3) && (0 != (bsnnumber[0] + bsnnumber[1] + bsnnumber[2])));

		return digitsAsNumber(bsnnumber);
	}
}
