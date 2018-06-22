package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DutchZipCodeAnonymizer}.
 *
 * @author Erik-Berndt Scheper
 */
public class DutchZipCodeAnonymizerTest {

    private DutchZipCodeAnonymizer anonymizer = new DutchZipCodeAnonymizer();
    private Pattern pattern = Pattern.compile("[1-9][0-9]{3} ?(?!SA|SD|SS)[A-Z]{2}$");

    @Test
    public void anonymize() {
        for (int i = 0; i < 1000000; i++) {
            String source = anonymizer.buildZipCode();
            assertThat(isValidZipCode(source), is(true));

            assertThat(source.length(), is(6));
            testInternal(6, source);
        }
    }

    private void testInternal(int size, String from) {
        Synonym synonym = anonymizer.anonymize(from, size, false);
        assertThat(synonym.getType(), sameInstance(anonymizer.getType()));

        String value = (String) synonym.getTo();
        assertThat(isValidZipCode(value), is(true));
        assertThat(synonym.isShortLived(), is(false));
    }

    private boolean isValidZipCode(String value) {
        return pattern.matcher(value).matches();
    }

}