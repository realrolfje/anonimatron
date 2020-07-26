package com.rolfje.anonimatron;

import java.util.StringTokenizer;

import junit.framework.TestCase;

import static org.junit.Assert.assertNotEquals;

public class LoremIpsumTest extends TestCase {

	public void testGetParagraphs() {
		String test = LoremIpsum.getParagraphs(3);

		assertFalse(test.startsWith(" "));
		assertFalse(test.startsWith("\n"));
		assertTrue(test.contains(" "));

		StringTokenizer t = new StringTokenizer(test, "\n");
		assertEquals(3, t.countTokens());
	}

	public void testGetWords() {
		String testText = LoremIpsum.getWords(50);
		StringTokenizer tokenizer = new StringTokenizer(testText);
		assertEquals(50, tokenizer.countTokens());

		String testText2 = LoremIpsum.getWords(50);
		StringTokenizer tokenizer2 = new StringTokenizer(testText2);
		assertEquals(50, tokenizer2.countTokens());

        assertNotEquals(testText, testText2);
	}

}
