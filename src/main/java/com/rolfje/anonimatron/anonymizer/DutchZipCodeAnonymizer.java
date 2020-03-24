package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Random;

/**
 * Generates valid Dutch zip codes.
 * <p>
 * The generated zip code has the following characteristics:
 * <ul>
 * <li>Starts with [1-9]</li>
 * <li>Followed by [0-9]{3}</li>
 * <li>Ends with two uppercase letters, that may <strong>NOT</strong> include:
 * <ul>
 * <li>SA</li>
 * <li>SD</li>
 * <li>SS</li>
 * </ul></li>
 * </ul>
 * <p>
 *
 * @author Erik-Berndt Scheper
 * @see <a href="https://nl.wikipedia.org/wiki/Postcodes_in_Nederland">https://nl.wikipedia.org/wiki/Postcodes_in_Nederland</a>
 */
public class DutchZipCodeAnonymizer implements Anonymizer {

	private static final String TYPE = "DUTCH_ZIP_CODE";

	private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CHARS_WITHOUT_ADS = "BCEFGHIJKLMNOPQRTUVWXYZ";
	protected static final Random RANDOM = new Random();

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Synonym anonymize(Object from, int size, boolean shortlived) {
		String result = buildZipCode();
		StringSynonym stringSynonym = new StringSynonym(TYPE, (String) from, result, shortlived);

		return stringSynonym;
	}

	String buildZipCode() {

		// generate a random integer from 0000 to 8999, then add 1000
		Integer pcNum = RANDOM.nextInt(9000) + 1000;
		return pcNum + buildPcAlpha();
	}

	private String buildPcAlpha() {

		char a1 = getCharacter(RANDOM, CHARS);
		char a2;

		if (a1 == 'S') {
			a2 = getCharacter(RANDOM, CHARS_WITHOUT_ADS);
		} else {
			a2 = getCharacter(RANDOM, CHARS);
		}

		return new StringBuilder().append(a1).append(a2).toString();
	}

	private char getCharacter(Random random, String chars) {
		return chars.charAt(random.nextInt(chars.length()));
	}
}
