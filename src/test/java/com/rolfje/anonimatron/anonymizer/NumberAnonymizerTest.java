package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class NumberAnonymizerTest {
    private NumberAnonymizer n = new NumberAnonymizer();
    private boolean shortlived = false;
    private Random r = new Random();

    @Test
    public void testAnonymieNumbers() {
        Object[] originals = {
                Integer.valueOf(r.nextInt()),
                Long.valueOf(r.nextLong()),
                new BigDecimal(r.nextDouble()),
                new Integer(r.nextInt())
        };

        for (Object originalAsNumber : originals) {
            Synonym synonym = n.anonymize(originalAsNumber, 9, shortlived);
            assertThat(originalAsNumber, is(synonym.getFrom()));
            assertThat(originalAsNumber, not(is(synonym.getTo())));
            assertThat(synonym.getTo(), is(instanceOf(originalAsNumber.getClass())));
            assertThat(synonym.isShortLived(), is(shortlived));
        }
    }
}