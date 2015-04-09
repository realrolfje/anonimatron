package com.rolfje.anonimatron.anonymizer;

import java.io.IOException;
import java.util.Random;

public class ElvenNameGenerator extends AbstractNameGenerator {
	private static final String TYPE = "ELVEN_NAME";
	private static final String SYLABLE_FILE = "elven-names.syl";

	private Random r = new Random();

	public ElvenNameGenerator() throws IOException {
		super(SYLABLE_FILE);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	String getName() {
		// Generate an Elven name of 2 to 5 sylables
		// This will result well over 1 million unique names.
		int syls = Math.round((r.nextFloat() * 5) + 2);
		return compose(syls);
	}
}
