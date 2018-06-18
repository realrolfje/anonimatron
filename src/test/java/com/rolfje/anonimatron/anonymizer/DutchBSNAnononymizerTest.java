package com.rolfje.anonimatron.anonymizer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.rolfje.anonimatron.synonyms.Synonym;

public class DutchBSNAnononymizerTest {

	private DutchBSNAnononymizer bsnAnonymizer = new DutchBSNAnononymizer();

	@Test
	public void testAnonymize() {

		for (int i = 0; i < 1000; i++) {
			String from = bsnAnonymizer.generateBSN(9);

			Synonym synonym = bsnAnonymizer.anonymize(from, 12);
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
		synonym = bsnAnonymizer.anonymize(number, 9);
		assertThat(number, is(synonym.getFrom()));
		validate(synonym);

		// Validate Long
		number = Long.parseLong(original);
		synonym = bsnAnonymizer.anonymize(number, 10);
		assertThat(number, is(synonym.getFrom()));
		validate(synonym);

		// Validate BigDecimal
		number = new BigDecimal(original);
		synonym = bsnAnonymizer.anonymize(number, 12);
		assertThat(number, is(synonym.getFrom()));
		validate(synonym);

		// Validate BigInteger
		number = new BigInteger(original);
		synonym = bsnAnonymizer.anonymize(number, 12);
		assertThat(number, is(synonym.getFrom()));
		validate(synonym);

	}

	@Test
	public void testLength() throws Exception {
		bsnAnonymizer.anonymize("dummy", 10000);
		bsnAnonymizer.anonymize("dummy", 9);

		try {
			bsnAnonymizer.anonymize("dummy", 8);
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
		assertThat(isValidBSN(synonym.getTo().toString()), is(true));

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
