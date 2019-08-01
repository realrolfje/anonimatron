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
        } else if (from instanceof Byte) {
            byte[] bytes = {0};
            r.nextBytes(bytes);
            return new Byte(bytes[0]);
        } else if (from instanceof Double) {
            return new Double(r.nextDouble());
        } else if (from instanceof Float) {
            return new Float(r.nextFloat());
        } else if (from instanceof Long) {
            return new Long(r.nextLong());
        } else if (from instanceof Short) {
            return Short.valueOf((short) r.nextInt(Short.MAX_VALUE));
        } else if (from instanceof BigDecimal) {
            return new BigDecimal(r.nextGaussian());
        } else if (from instanceof BigInteger) {
            return BigInteger.valueOf(r.nextLong());
        } else {
            throw new IllegalArgumentException(from.getClass().getSimpleName() + " is not supported for " + this.getType());
        }
    }
}
