package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.NumberSynonym;
import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.YEAR;

/**
 * Generates valid Belgian "Rijks Register Nummer" or "INSZ Nummer", a social
 * security number uniquely identifying civilians to the governement.
 * <p>
 * A BSN Number is 11 digits long and is composed of:
 *         * jjmmdd birth date
 *         * sequence number odd for male, even for female
 *         * the remainder modulo 97 of the 9 digits of the number of segments 1 and 2
 * <p>
 * See https://nl.wikipedia.org/wiki/Rijksregisternummer
 */
public class BelgianINSZAnononymizer implements Anonymizer {
    private static final int LENGTH = 11;

    @Override
    public String getType() {
        return "RIJKSREGISTERNUMMER";
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean isShortLived) {
        if (size < LENGTH) {
            throw new UnsupportedOperationException(
                    "Cannot generate a RRN that fits in a " + size + " character string. Must be " + LENGTH
                            + " characters or more.");
        }

        String newRRN = generateNonIdenticalRRN(from);

        if (from instanceof Number) {
            return new NumberSynonym(
                    getType(),
                    (Number) from,
                    asNumber(newRRN, (Number) from),
                    isShortLived);
        } else if (from instanceof String) {
            return new StringSynonym(
                    getType(),
                    from.toString(),
                    newRRN,
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

    String generateNonIdenticalRRN(Object from) {
        String newRRN;
        String oldRRN = from.toString();

        do {
            // Never generate identical number
            newRRN = generateRRN(from);
        } while (oldRRN.equals(newRRN));
        return newRRN;
    }

    /**
     * Checks validity of the given BSN number. The number may be internally
     * pre-padded with zeroes to make it 9 digits long. This padded number
     * is not returned.
     * <p>
     * See https://nl.wikipedia.org/wiki/Burgerservicenummer
     *
     * @param rijksRegisterNummer
     * @return <code>true</code> if the given BSN number meets the specifications
     * as described in https://nl.wikipedia.org/wiki/Burgerservicenummer
     */
    public static boolean isValidRRN(String rijksRegisterNummer) {
        // Number must be at 11 characters in length,
        if (rijksRegisterNummer == null || rijksRegisterNummer.length() != 11) {
            return false;
        }

        return isRRNMod97(rijksRegisterNummer);
    }

    //
    // maand en dag van geboortedatum kunnen wel degelijk nul zijn wanneer geboortedatum niet exact gekend is
    // we need "BirthdayAnonymizer, to generate days within a credible range, but, for now it is ok.
    //
    String generateRRN() {
        return generateRRN("00000000000");
    }

    String generateRRN(Object from) {
        String oldRRN;
        if (from instanceof Number) {
            oldRRN = from.toString();
        }
        else {
            oldRRN = (String) from;
        }

        // Keep dateAnonymizer, but give people a credible birth date :-)
        DateAnonymizer dateAnonymizer = new DateAnonymizer();
        Synonym newBirthDate = dateAnonymizer.anonymize(new java.sql.Date(new Integer(oldRRN.substring(0,1)),
                new Integer(oldRRN.substring(2,3)),
                new Integer(oldRRN.substring(4,5))), 6, false);
        Date birthDate = (Date) newBirthDate.getTo();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthDate);
        int year = calendar.get(YEAR);

        while (year < 1900) year += 100;
        int yy = year < 2000? (year - 1900) : (year -= 2000);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int[] randomDigits = getRandomDigits(3);

        StringBuilder rrnBuilder = new StringBuilder();
        rrnBuilder.append(String.format("%02d",yy));
        rrnBuilder.append(String.format("%02d",month));
        rrnBuilder.append(String.format("%02d",day));
        rrnBuilder.append(String.format("%03d",digitsAsInteger(randomDigits)));


        int birthSeq = Integer.valueOf(rrnBuilder.toString());

        if (year >= 2000) {
            birthSeq += 2000000000;
        }

        int check = 97 - (birthSeq % 97);

        rrnBuilder.append(String.format("%02d", check));
        String rijksRegisterNummer = rrnBuilder.toString();
        return rijksRegisterNummer;
    }

    private static boolean isRRNMod97(String rijksRegisterNummer) {

        int check = new Integer(rijksRegisterNummer.substring(0,9)).intValue();
        int remainder = new Integer(rijksRegisterNummer.substring(9,11)).intValue();
        if ((check % 97) == (97 - remainder)) return true;              // 20th century children
        // 21st century children
        if (((check + 2000000000) % 97) == (97 - remainder)) return true;
        return false;
    }

    // subclassing : to avoid duplicating code:
    // an abstract parent of AbstractElevenProofAnonymizer should be created
    // but for now, keep it pragmatic

    private int[] getRandomDigits(int numberOfDigits) {
        int[] randomDigits = new int[numberOfDigits];
        for (int i = 0; i < randomDigits.length; i++) {
            randomDigits[i] = (int) Math.floor(Math.random() * 10);
        }
        return randomDigits;
    }
    protected int digitsAsInteger(int[] digits) {
        int result = 0;
        for (int i = 0; i < digits.length; i++) {
            result += (int) Math.pow(10, (digits.length - i - 1)) * digits[i];
        }
        return result;
    }
}
