package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Random;

/**
 * Generates format valid Uk Post codes.
 * See https://en.wikipedia.org/wiki/Postcodes_in_the_United_Kingdom
 * and See https://stackoverflow.com/a/164994
 * <p>
 * Let me know if this isn't correct in practice; UK postal codes seem
 * to have a very strange organically grown format.
 */
public class UkPostCodeAnonymizer implements Anonymizer {
    private final Random random = new Random();

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    @Override
    public String getType() {
        return "UK_POST_CODE";
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        if (from == null) {
            return new StringSynonym(getType(), null, null, shortlived);
        }

        if (size < 8) {
            throw new UnsupportedOperationException(
                    "Random UK postal codes can be 9 characters in length and will "
                            + "not always fit configured size of " + size + ".");
        }

        String to = buildZipCode();
        if (to.length() > size) {
            throw new UnsupportedOperationException(
                    "Generated postalcode was " + to.length() + " characters and does not fit "
                            + size + " characters.");
        }

        return new StringSynonym(
                getType(),
                (String) from,
                to,
                shortlived
        );
    }

    String buildZipCode() {
        if (random.nextInt(1_673_945_000) == 0) {
            return buildZipCodeFlavor1();
        } else {
            return buildZipCodeFlavor2();
        }
    }

    // Length 7
    // ^([Gg][Ii][Rr] 0[Aa]{2})$
    String buildZipCodeFlavor1() {
        if (random.nextBoolean()) {
            return "GIR0AA";
        } else {
            return "GIR 0AA";
        }
    }

    String buildZipCodeFlavor2() {
        // Total 247.625 * 10 * 26 * 26 = 1.673.945.000 combinations
        StringBuilder b = new StringBuilder();

        // Prefix:  Total prefix set is 247.625 combinations
        if (random.nextInt(96) == 0) { // one in 96 is a p1
            b.append(p1()); // 2.600 combinations
        } else {
            if (random.nextInt(4) == 0) { // if not a p1, one in 4 is a p2a
                b.append(p2a()); // 62.400 combinations
            } else {
                if (random.nextInt(27) == 0) { // if not a p2a, one in 27 is a p2ba
                    b.append(p2ba()); // 6.760 combinations
                } else {
                    b.append(p2bb()); // 178.464 combinations
                }
            }
        }

        if (random.nextBoolean()) {
            b.append(' ');
        }

        b.append(getRandomCharacter(DIGITS));
        b.append(getRandomCharacter(CHARACTERS));
        b.append(getRandomCharacter(CHARACTERS));
        return b.toString();
    }

    // Length 3
    // 26 * 100 = 2.600 combinations
    // ^((([A-Za-z][0-9]{1,2})$
    String p1() {
        StringBuilder b = new StringBuilder();
        b.append(getRandomCharacter(CHARACTERS));
        b.append(getRandomCharacter(DIGITS));

        if (random.nextBoolean()) {
            b.append(getRandomCharacter(DIGITS));
        }

        return b.toString();
    }

    // Length 4
    // 26 * 24 * 100 = 62.400 combinations
    // ([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})
    String p2a() {
        StringBuilder b = new StringBuilder();
        b.append(getRandomCharacter(CHARACTERS));
        b.append(getRandomCharacter("ABCDEFGHJKLMNOPQRSTUVWXY"));
        b.append(getRandomCharacter(DIGITS));
        if (random.nextBoolean()) {
            b.append(getRandomCharacter(DIGITS));
        }
        return b.toString();
    }

    // Length 3
    // 26 * 10 * 26 = 6.760 combinations
    // ([A-Za-z][0-9][A-Za-z])
    String p2ba() {
        return String.valueOf(getRandomCharacter(CHARACTERS)) +
                getRandomCharacter(DIGITS) +
                getRandomCharacter(CHARACTERS);
    }

    // Length 3 to 4
    // 26 * 24 * 11 * 26 = 178.464 combinations
    //([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z])
    String p2bb() {
        StringBuilder b = new StringBuilder();
        b.append(getRandomCharacter(CHARACTERS));
        b.append(getRandomCharacter("ABCDEFGHJKLMNOPQRSTUVWXY"));
        if (random.nextBoolean()) {
            b.append(getRandomCharacter(DIGITS));
        }
        b.append(getRandomCharacter(CHARACTERS));
        return b.toString();
    }

    private char getRandomCharacter(String characters) {
        return characters.charAt(random.nextInt(characters.length()));
    }
}
