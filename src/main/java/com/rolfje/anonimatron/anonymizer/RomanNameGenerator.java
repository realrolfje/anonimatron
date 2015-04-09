package com.rolfje.anonimatron.anonymizer;

import java.io.IOException;
import java.util.Random;

public class RomanNameGenerator extends AbstractNameGenerator {
	private static final String TYPE = "ROMAN_NAME";
	private static final String SYLABLE_FILE = "roman-names.syl";

	private Random r = new Random();

	@Override
	public String getType() {
		return TYPE;
	}

	public RomanNameGenerator() throws IOException {
		super(SYLABLE_FILE);
	}

	@Override
	String getName() {
		// Generate a Roman name of 2 to 5 sylables
		// This will result in approx. 897046 unique names.
		int syls = Math.round((r.nextFloat() * 5) + 2);
		return compose(syls);
	}
}
