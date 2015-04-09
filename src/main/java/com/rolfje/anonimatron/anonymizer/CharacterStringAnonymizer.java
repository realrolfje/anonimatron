package com.rolfje.anonimatron.anonymizer;

import java.util.Random;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class CharacterStringAnonymizer implements Anonymizer {

	protected String CHARS;

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
	public Synonym anonymize(Object from, int size) {
		int length = from.toString().length();
		Random r = new Random();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(CHARS.charAt(r.nextInt(CHARS.length())));
		}

		String to = sb.toString();

		StringSynonym stringSynonym = new StringSynonym();
		stringSynonym.setFrom(from);
		stringSynonym.setType(getType());
		stringSynonym.setTo(to);

		return stringSynonym;
	}

}
