package com.rolfje.anonimatron.anonymizer;

import junit.framework.TestCase;

import java.io.Serializable;

public class HasherTest extends TestCase {
	public void testBase64Hash() throws Exception {
		assertEquals(
				new Hasher("salt").base64Hash("piep"),
				new Hasher("salt").base64Hash("piep")
		);
	}

}