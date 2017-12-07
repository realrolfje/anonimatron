package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;

public class SynonymCacheTest extends TestCase {

	public void testNoHashing() throws Exception {
		SynonymCache synonymCache = new SynonymCache();

		StringSynonym originalSynonym = new StringSynonym("type", "from", "to");

		// Store and retrieve
		synonymCache.put(originalSynonym);
		Synonym storedSynonym = synonymCache.get(originalSynonym.getType(), originalSynonym.getFrom());

		// Should not be hashed
		assertEquals(originalSynonym.getType(), storedSynonym.getType());
		assertEquals(originalSynonym.getFrom(), storedSynonym.getFrom());
		assertEquals(originalSynonym.getTo(), storedSynonym.getTo());
	}

	public void testHashing() throws Exception {
		SynonymCache synonymCache = new SynonymCache();
		synonymCache.setHasher(new Hasher("testhash"));

		StringSynonym originalSynonym = new StringSynonym("type", "from", "to");

		// Store and retrieve
		synonymCache.put(originalSynonym);
		Synonym storedSynonym = synonymCache.get(originalSynonym.getType(), originalSynonym.getFrom());

		// Should not be same
		assertFalse(originalSynonym.getFrom().equals(storedSynonym.getFrom()));

		// Should be same
		assertEquals(originalSynonym.getType(), storedSynonym.getType());
		assertEquals(originalSynonym.getTo(), storedSynonym.getTo());
	}
}