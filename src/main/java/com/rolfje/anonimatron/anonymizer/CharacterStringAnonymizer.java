package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Map;
import java.util.Random;

/**
 * Generates an output string based on the configured characters
 * to use.
 */
public class CharacterStringAnonymizer implements Anonymizer {

	public static String PARAMETER = "characters";

	protected String CHARS;

	protected static final Random RANDOM = new Random();

	public CharacterStringAnonymizer() {
		CHARS = getDefaultCharacterString();
	}

	protected String getDefaultCharacterString() {
		return "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	}
	
	@Override
	public String getType() {
		return "RANDOMCHARACTERS";
	}

	@Override
	public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
		if (parameters == null || !parameters.containsKey("characters")) {
			throw new UnsupportedOperationException("Please provide '" + PARAMETER + "' as configuration parameter.");
		}
		return anonymize(from, size, shortlived, parameters.get(PARAMETER));
	}

	@Override
	public Synonym anonymize(Object from, int size, boolean shortlived) {
		return anonymize(from, size, shortlived, CHARS);
	}

	private Synonym anonymize(Object from, int size, boolean shortlived, String characters) {
		String fromString = from.toString();

		int length = fromString.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(characters.charAt(RANDOM.nextInt(characters.length())));
		}

		String to = sb.toString();

		StringSynonym stringSynonym = new StringSynonym(
				getType(),
				fromString,
				to,
				shortlived
		);

		return stringSynonym;
	}

}
