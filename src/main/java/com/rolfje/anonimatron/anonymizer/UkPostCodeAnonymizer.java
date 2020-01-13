package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Random;

/**
 * Generates format valid Uk Post codes.
 * See https://en.wikipedia.org/wiki/Postcodes_in_the_United_Kingdom
 */
public class UkPostCodeAnonymizer implements Anonymizer {
    private Random random = new Random();

    private static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String DIGITS = "0123456789";

    @Override
    public String getType() {
        return "UK_POST_CODE";
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        String result = buildZipCode();
        StringSynonym s = new StringSynonym();
        s.setFrom((String) from);
        s.setTo((String) result);
        return s;
    }

    String buildZipCode() {

        String area = "" + getRandomCharacter(CHARACTERS) + getRandomCharacter(CHARACTERS);

        // Not implemented: The one digit districts
        String district = "" + getRandomCharacter(DIGITS) + getRandomCharacter(CHARACTERS + DIGITS);

        String sector = "" + getRandomCharacter(DIGITS);
        String unit = "" + getRandomCharacter(CHARACTERS) + getRandomCharacter(CHARACTERS);

        return area + district + " " + sector + unit;
    }

    private char getRandomCharacter(String characters) {
        return characters.charAt(random.nextInt(characters.length()));
    }
}
