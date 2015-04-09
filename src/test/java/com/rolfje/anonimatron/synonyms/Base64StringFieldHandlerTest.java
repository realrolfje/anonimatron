package com.rolfje.anonimatron.synonyms;

import junit.framework.TestCase;

public class Base64StringFieldHandlerTest extends TestCase {

	Base64StringFieldHandler handler = new Base64StringFieldHandler();

	public void testStringHandling() throws Exception {
		testConversion("\t0123456789 The quick brown fox jumped over the lazy dog.\n\r");
		testConversion(String.valueOf(SynonymMapperTest.ILLEGALSTRINGCHARACTERS));
		testConversion(null);
		testConversion("");
		testConversion(" ");
	}

	private void testConversion(String testString) {
		Object convertedToBase64 = handler.convertUponGet(testString);
		Object convertedToString = handler.convertUponSet(convertedToBase64);
		assertEquals(testString, convertedToString);
	}

	public void testNull() throws Exception {
		assertNull(handler.convertUponGet(null));
		assertNull(handler.convertUponSet(null));
	}
}
