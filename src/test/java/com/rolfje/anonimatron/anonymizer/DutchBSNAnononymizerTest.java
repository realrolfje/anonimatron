package com.rolfje.anonimatron.anonymizer;

import junit.framework.TestCase;

import com.rolfje.anonimatron.synonyms.Synonym;

public class DutchBSNAnononymizerTest extends TestCase {

	private DutchBSNAnononymizer bsnAnonymizer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bsnAnonymizer = new DutchBSNAnononymizer();
	}

	public void testAnonymize() {

		for (int i = 0; i < 1000; i++) {
			String from = bsnAnonymizer.generateBSN(9);

			Synonym s = bsnAnonymizer.anonymize(from, 12);

			assertEquals(from, s.getFrom());
			assertFalse(s.getFrom().equals(s.getTo()));
			assertNotNull(s.getType());
			assertTrue(s.getTo() + " is not a valid BSN",
					isValidBSN((String) s.getTo()));
		}
	}

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

	public void testGenerateBSN() {
		for (int i = 0; i < 1000; i++) {
			String bsn = bsnAnonymizer.generateBSN(9);
			assertTrue(bsn+" is not a valid BSN", isValidBSN(bsn));
		}
	}

	public static boolean isValidBSN(String burgerServiceNummer) {
		if (burgerServiceNummer == null || burgerServiceNummer.length() != 9) {
			return false;
		}

		int[] digits = new int[9];
		for (int i = 0; i < digits.length; i++) {
			digits[i] = Integer.valueOf(""+burgerServiceNummer.charAt(i));
		}

		int check = 0;
		for (int i = 0; i < digits.length; i++) {
			check += digits[i] * (digits.length - i);
		}

		return (check % 11) == 0;
	}

}
