package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.NumberSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.commons.lang3.StringUtils;

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
            return new RandomBigDecimal().next();
        } else if (from instanceof BigInteger) {
            return BigInteger.valueOf(r.nextLong());
        } else {
            throw new IllegalArgumentException(from.getClass().getSimpleName() + " is not supported for " + this.getType());
        }
    }

    private class RandomBigDecimal {
        BigDecimal max = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal min = BigDecimal.valueOf(Double.MIN_VALUE);

        public RandomBigDecimal() {
        }

        public RandomBigDecimal(String min, String max) {
            if (StringUtils.isNotEmpty(min)) {
                this.min = new BigDecimal(min);
            }

            if (StringUtils.isNotEmpty(max)) {
                this.max = new BigDecimal(max);
            }
        }

        BigDecimal next() {
            // TODO imperfect; does not take into account decimal places.
            // default implementation could take decimal places from the original,
            // so that amounts stored as floats (which is a TERRIBLE idea)
            // result in new amounts with the same number of decimal places.

            BigDecimal high = max.multiply(BigDecimal.valueOf(r.nextDouble()));
            BigDecimal low = min.multiply(BigDecimal.valueOf(r.nextDouble()));
            return high.add(low);
        }
    }
}
