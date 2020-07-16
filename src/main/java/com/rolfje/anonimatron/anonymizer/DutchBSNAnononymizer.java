package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.NumberSynonym;
import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Generates valid Dutch "Burger Service Nummer" or "SOFI Nummer", a social
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

    String generateNonIdenticalBSN(Object from) {
        String newBSN;
        String oldBSN = from.toString();

        do {
            // Never generate identical number
            newBSN = generateBSN(LENGTH);
        } while (oldBSN.equals(newBSN));
        return newBSN;
    }

    /**
     * Checks validity of the given BSN number. The number may be internally
     * pre-padded with zeroes to make it 9 digits long. This padded number
     * is not returned.
     * <p>
     * See https://nl.wikipedia.org/wiki/Burgerservicenummer
     *
     * @param burgerServiceNummer
     * @return <code>true</code> if the given BSN number meets the specifications
     * as described in https://nl.wikipedia.org/wiki/Burgerservicenummer
     */
    public static boolean isValidBSN(String burgerServiceNummer) {
        // Number must be at least 7 at maximum 9 characters in length,
        if (burgerServiceNummer == null
                || burgerServiceNummer.length() > 9
                || burgerServiceNummer.length() < 7) {
            return false;
        }

        // Numbers shorter than 9 characters are padded with zeroes.
        burgerServiceNummer = StringUtils.leftPad(burgerServiceNummer, 9, "0");

        // The leftmost character dan not be higher than 3
        if (burgerServiceNummer.charAt(0) > '3') {
            return false;
        }


        return isBSNElevenProof(burgerServiceNummer);
    }

    String generateBSN(int numberOfDigits) {
        if (numberOfDigits > 9 || numberOfDigits < 7) {
            throw new IllegalStateException("Can not generate a BSN number with " + numberOfDigits + " characters.");
        }

        int[] randomDigits = getRandomDigits(numberOfDigits);

        // Left most character can never be higher than 3.
        if (numberOfDigits == 9) {
            randomDigits[0] = (int) Math.floor(Math.random() * 3);
        }

        // Pad with zeroes to get to 9 digits.
        String burgerServiceNummer = String.format("%09d", digitsAsInteger(randomDigits));

        while (!isBSNElevenProof(burgerServiceNummer)) {
            int asNumber = Integer.valueOf(burgerServiceNummer);
            int correction = elevenProofSum(burgerServiceNummer) % 11;
            burgerServiceNummer = String.format("%09d", asNumber + correction);
        }

        return burgerServiceNummer;
    }

    private static boolean isBSNElevenProof(String burgerServiceNummer) {
        int check = elevenProofSum(burgerServiceNummer);
        return (check % 11) == 0;
    }

    /**
     * Specific 11 proof algorithm for BSN where the last digit is negated.
     * (see https://nl.wikipedia.org/wiki/Burgerservicenummer#11-proef)
     */
    private static int elevenProofSum(String burgerServiceNummer) {
        int[] factors = {9, 8, 7, 6, 5, 4, 3, 2, -1};
        int check = 0;
        for (int i = 0; i < burgerServiceNummer.length(); i++) {
            int digit = Integer.parseInt("" + burgerServiceNummer.charAt(i));
            check += digit * factors[i];
        }
        return check;
    }
}
