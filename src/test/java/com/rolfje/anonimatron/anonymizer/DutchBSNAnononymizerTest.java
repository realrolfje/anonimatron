package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DutchBSNAnononymizerTest {

	private DutchBSNAnononymizer bsnAnonymizer = new DutchBSNAnononymizer();

	private String original;
	boolean shortlived = true;


	@Before
	public void setUp() throws Exception {
		original = bsnAnonymizer.generateBSN(9);
	}

	@Test
	public void testAnonymizeNumbers() {
		Object[] originals = {
				Integer.valueOf(original),
				Long.valueOf(original),
				new BigDecimal(original),
				new Integer(original)
		};

		for (Object originalAsNumber : originals) {
			Synonym synonym = bsnAnonymizer.anonymize(originalAsNumber, 9, shortlived);
			assertThat(originalAsNumber, is(synonym.getFrom()));
			assertThat(original, not(is(synonym.getTo())));
			assertThat(synonym.getTo(), is(instanceOf(originalAsNumber.getClass())));
			assertThat(
					"BSN " + synonym.getTo().toString() + " is invalid",
					isValidBSN(synonym.getTo().toString()), is(true)
			);
			assertThat(synonym.isShortLived(), is(shortlived));
		}
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

	}

	private boolean isValidBSN(String burgerServiceNummer) {
		// Number must be at least 7 at maximum 9 characters in length,
		if (burgerServiceNummer == null
				|| burgerServiceNummer.length() > 9
				|| burgerServiceNummer.length() < 7) {
			return false;
		}

		// Numbers shorter than 9 characters are padded with zeroes.
		burgerServiceNummer = StringUtils.leftPad(burgerServiceNummer, 9, "0");

		// The leftmost character dan not be higher than 3
		if (burgerServiceNummer.charAt(0) > '3') {
			return false;
		}

		// Check the digits to be 11 Proof
		int check = 0;
		for (int i = 0; i < burgerServiceNummer.length(); i++) {
			int digit = Integer.valueOf("" + burgerServiceNummer.charAt(i));
			check += digit * (burgerServiceNummer.length() - i);
		}

		return (check % 11) == 0;
	}
}
