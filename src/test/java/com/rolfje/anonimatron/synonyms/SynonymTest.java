package com.rolfje.anonimatron.synonyms;

import junit.framework.TestCase;

public class SynonymTest extends TestCase {

    public void testEqualsHashcode() {

        StringSynonym a = new StringSynonym(
                "Bunk",
                "Fpp",
                "Bar",
                false
        );

        StringSynonym b = new StringSynonym(
                "Bunk",
                "Fpp",
                "Bar",
                false
        );

        assertNotSame(a, b);
        assertEquals(a, b);

    }

}
