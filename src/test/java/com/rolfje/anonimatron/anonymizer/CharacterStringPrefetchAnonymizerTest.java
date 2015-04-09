package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;

import junit.framework.TestCase;

public class CharacterStringPrefetchAnonymizerTest extends TestCase {

	CharacterStringPrefetchAnonymizer anonimyzer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		anonimyzer = new CharacterStringPrefetchAnonymizer();
	}

	public void testPrefetch() throws Exception {
		String sourceData = "ABC";
		anonimyzer.prefetch(sourceData);

		String from = "TEST1";
		Synonym synonym = anonimyzer.anonymize(from, 5);

		String to = (String)synonym.getTo();
		assertEquals("from and to string lengths did not match", from.length(), to.length());

		for (int i = 0; i < to.length(); i++) {
			assertTrue("'to' string contained charachters which were not in the source data."
				, sourceData.indexOf(to.charAt(i)) > -1);
		}
	}

	public void testPrefetchNull() throws Exception {
		anonimyzer.prefetch(null);
		String from = "DUMMY";
		Synonym synonym = anonimyzer.anonymize(from, 5);
		
		String to = (String)synonym.getTo();
		assertEquals("from and to string lengths did not match", from.length(), to.length());
	}
}
