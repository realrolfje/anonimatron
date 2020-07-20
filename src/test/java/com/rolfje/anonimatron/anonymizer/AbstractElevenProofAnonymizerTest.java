package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AbstractElevenProofAnonymizerTest {

    AbstractElevenProofAnonymizer testdummy;

    @Before
    public void setUp() {
        testdummy = new AbstractElevenProofAnonymizer() {

            @Override
            public String getType() {
                return null;
            }

            @Override
            public Synonym anonymize(Object from, int size, boolean shortlived) {
                return null;
            }

            @Override
            public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
                return null;
            }
        };
    }

    @Test
    public void digitsAsInteger() {
        assertEquals(10, testdummy.digitsAsInteger(new int[]{1,0}));
        assertEquals(1234, testdummy.digitsAsInteger(new int[]{1,2,3,4}));
        assertEquals(1, testdummy.digitsAsInteger(new int[]{0,0,0,1}));
        assertEquals(0, testdummy.digitsAsInteger(new int[]{}));
    }
}