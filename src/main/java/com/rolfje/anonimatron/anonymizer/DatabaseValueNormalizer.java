package com.rolfje.anonimatron.anonymizer;

import java.lang.reflect.Method;

public final class DatabaseValueNormalizer {
    private static final String POSTGRESQL_OBJECT_CLASS = "org.postgresql.util.PGobject";

    private DatabaseValueNormalizer() {
    }

    public static Object normalize(Object value) {
        if (value != null && POSTGRESQL_OBJECT_CLASS.equals(value.getClass().getName())) {
            return normalizePostgreSqlObject(value);
        }

        return value;
    }

    /**
     * Extracts the string value from a PostgreSQL PGobject. This helps in cases where
     * the PostgreSQL JDBC driver return a citext value for example.
     */
    private static Object normalizePostgreSqlObject(Object value) {
        /*
         * Keep the PostgreSQL JDBC driver optional at compile time. Reflection lets
         * us call PGobject.getValue() without importing the PostgreSQL class.
         */
        try {
            Method getValue = value.getClass().getMethod("getValue");
            return getValue.invoke(value);
        } catch (ReflectiveOperationException e) {
            // Fall back to the original if reflection fails
            return value;
        }
    }
}
