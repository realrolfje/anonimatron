package com.rolfje.anonimatron.synonyms;

import junit.framework.TestCase;

public class SynonymTest extends TestCase {

	public void testEqualsHashcode() throws Exception {

		StringSynonym a = new StringSynonym();
		a.setType("Bunk");
		a.setFrom("Foo");
		a.setTo("Bar");

		StringSynonym b = new StringSynonym();
		b.setType("Bunk");
		b.setFrom("Foo");
		b.setTo("Bar");

		assertNotSame(a, b);
		assertEquals(a, b);

	}

}
