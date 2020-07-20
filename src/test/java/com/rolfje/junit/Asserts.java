package com.rolfje.junit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Asserts {

    public static void assertAnyOf(Object expected, Object... values) {
        for (Object value : values) {
            if (expected.equals(value)) {
                return;
            }
        }
        fail(expected.toString() + " did not match any of " + values.toString());
    }

    public static void assertInstanceOf(Class<?> expectedClass, Object actualClass) {
        Class<?> matchable = matchableClass(expectedClass);
        assertTrue("Class " + actualClass.getClass().getSimpleName() + " is not an instance of " + expectedClass.getSimpleName(),
                matchable.isInstance(actualClass));
    }

    private static Class<?> matchableClass(Class<?> expectedClass) {
        if (boolean.class.equals(expectedClass)) return Boolean.class;
        if (byte.class.equals(expectedClass)) return Byte.class;
        if (char.class.equals(expectedClass)) return Character.class;
        if (double.class.equals(expectedClass)) return Double.class;
        if (float.class.equals(expectedClass)) return Float.class;
        if (int.class.equals(expectedClass)) return Integer.class;
        if (long.class.equals(expectedClass)) return Long.class;
        if (short.class.equals(expectedClass)) return Short.class;
        return expectedClass;
    }
}
