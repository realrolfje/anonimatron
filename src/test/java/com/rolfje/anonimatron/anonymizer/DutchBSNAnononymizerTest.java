package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DutchBSNAnononymizerTest {

	private DutchBSNAnononymizer bsnAnonymizer = new DutchBSNAnononymizer();

	@Test
	public void testAnonymize() {

		for (int i = 0; i < 1000; i++) {
			String from = bsnAnonymizer.generateBSN(9);

			Synonym synonym = bsnAnonymizer.anonymize(from, 12, false);
			assertThat(from, is(synonym.getFrom()));
			validate(synonym);
		}
	}

	@Test
	public void testAnonymizeNumber() {
		Number number;
		Synonym synonym;

		String original = bsnAnonymizer.generateBSN(9);

		// Validate Integer
		number = Integer.parseInt(original);
		synonym = bsnAnonymizer.anonymize(number, 9, false);
		assertThat(number, is(synonym.getFrom()));
		validate(synonym);

		// Validate Long
		number = Long.parseLong(original);
		synonym = bsnAnonymizer.anonymize(number, 10, false);
		assertThat(number, is(synonym.getFrom()));
		validate(synonym);

		// Validate BigDecimal
		number = new BigDecimal(original);
		synonym = bsnAnonymizer.anonymize(number, 12, false);
		assertThat(number, is(synonym.getFrom()));
		validate(synonym);

		// Validate BigInteger
		number = new BigInteger(original);
		synonym = bsnAnonymizer.anonymize(number, 12, false);
		assertThat(number, is(synonym.getFrom()));
		validate(synonym);

	}

	@Test
	public void testLength() throws Exception {
		bsnAnonymizer.anonymize("dummy", 10000, false);
		bsnAnonymizer.anonymize("dummy", 9, false);

		try {
			bsnAnonymizer.anonymize("dummy", 8, false);
			fail("should throw exception");
		} catch (UnsupportedOperationException e) {
			// ok
		}
	}

	@Test
	public void testGenerateBSN() {
		for (int i = 0; i < 1000; i++) {
			String bsn = bsnAnonymizer.generateBSN(9);
			assertThat(isValidBSN(bsn), is(true));
		}
	}

	private void validate(Synonym synonym) {
		assertThat(synonym.getFrom(), is(not(synonym.getTo())));
		assertThat(synonym.getType(), notNullValue());

		Object value = synonym.getTo();
		String stringValue;

		if (value instanceof Integer) {
			stringValue = String.format("%09d", (Integer) value);
		} else if (value instanceof Long) {
			stringValue = String.format("%09d", (Long) value);
		} else if (value instanceof BigInteger) {
			stringValue = String.format("%09d", ((BigInteger) value).longValue());
		} else if (value instanceof BigDecimal) {
			stringValue = String.format("%09d", ((BigDecimal) value).longValue());
		} else {
			stringValue = value.toString();
		}

		assertThat("BSN " + stringValue + " is invalid", isValidBSN(stringValue), is(true));
	}

	private boolean isValidBSN(String burgerServiceNummer) {
		if (burgerServiceNummer == null || burgerServiceNummer.length() != 9) {
			return false;
		}

		int[] digits = new int[9];
		for (int i = 0; i < digits.length; i++) {
			digits[i] = Integer.valueOf("" + burgerServiceNummer.charAt(i));
		}

		int check = 0;
		for (int i = 0; i < digits.length; i++) {
			check += digits[i] * (digits.length - i);
		}

		return (check % 11) == 0;
	}

}
