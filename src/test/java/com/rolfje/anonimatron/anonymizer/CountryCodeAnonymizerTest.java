package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;

public class CountryCodeAnonymizerTest extends TestCase {
	public void testAnonymize() throws Exception {

		testInternal(2, "EN");
		testInternal(3, "NLD");
		testInternal(4, "BLR");
	}

	private void testInternal(int size, String from) {
		CountryCodeAnonymizer countryCodeAnonymizer = new CountryCodeAnonymizer();
		Synonym nld = countryCodeAnonymizer.anonymize(from, size);
		assertEquals(countryCodeAnonymizer.getType(), nld.getType());
		assertEquals(from, nld.getFrom());
		assertFalse(from.equals(nld.getTo()));
		assertEquals(size, ((String) nld.getTo()).length());
	}

}