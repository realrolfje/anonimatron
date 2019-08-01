package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.NumberSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class NumberAnonymizer implements Anonymizer {
    private static final String TYPE = "NUMBER";
    private Random r = new Random();

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        return new NumberSynonym(TYPE, (Number) from, randomNumber((Number) from), shortlived);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    private Number randomNumber(Number from) {
        if (from == null) {
            return null;
        } else if (from instanceof Integer) {
            return new Integer(r.nextInt());
        } else if (from instanceof Long) {
            return new Long(r.nextLong());
        } else if (from instanceof BigDecimal) {
            return new BigDecimal(r.nextGaussian());
        } else if (from instanceof BigInteger) {
            return BigInteger.valueOf(r.nextLong());
        } else {
            throw new IllegalArgumentException(from.getClass().getSimpleName() + " is not supported for " + this.getType());
        }
    }
}
