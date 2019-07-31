package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.NumberSynonym;
import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.math.BigDecimal;
import java.math.BigInteger;

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
	public Synonym anonymize(Object from, int size, boolean isShortLived) {
		if (size < LENGTH) {
			throw new UnsupportedOperationException(
					"Cannot generate a BSN that fits in a " + size + " character string. Must be " + LENGTH
							+ " characters or more.");
		}

		String newBSN = generateNonIdenticalBSN(from);

		if (from instanceof Number) {
			return new NumberSynonym(
					getType(),
					(Number) from,
					asNumber(newBSN, (Number) from),
					isShortLived);
		} else if (from instanceof String) {
			return new StringSynonym(
					getType(),
					from.toString(),
					newBSN,
					isShortLived);
		} else {
			throw new IllegalArgumentException(
					"Type " + from.getClass().getSimpleName() + " is not supported for " + this.getType());
		}
	}


	private Number asNumber(String value, Number from) {
		if (from instanceof Integer) {
			return Integer.parseInt(value);
		}
		else if (from instanceof Long) {
			return Long.parseLong(value);
		}
		else if (from instanceof BigDecimal) {
			return new BigDecimal(value);
		}
		else if (from instanceof BigInteger) {
			return new BigInteger(value);
		}
		else {
			throw new IllegalArgumentException(from.getClass().getSimpleName() + " is not supported for " + this.getType());
		}
	}

	String generateNonIdenticalBSN(Object from) {
		String newBSN;
		String oldBSN = from.toString();

		do {
			// Never generate identical number
			newBSN = generateBSN(LENGTH);
		} while (oldBSN.equals(newBSN));
		return newBSN;
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
