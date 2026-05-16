package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnonymizerServiceTest extends TestCase {
    AnonymizerService anonService;

    protected void setUp() throws Exception {
        anonService = new AnonymizerService();
        anonService.registerAnonymizers(
                Collections.singletonList(
                        FixedValueAnonymizer.class.getName()));
    }

    public void testStringAnonymizer() {
        List<Object> fromList = new ArrayList<>();
        fromList.add("String 1");
        fromList.add("String 2");
        fromList.add("String 3");

        String type = new StringAnonymizer().getType();

        testAnonymizer(fromList, type, type);
    }

    public void testUUIDAnonymizer() {
        List<Object> fromList = new ArrayList<>();
        fromList.add("String 1");
        fromList.add("String 2");
        fromList.add("String 3");

        String type = new UUIDAnonymizer().getType();

        testAnonymizer(fromList, type, type);
    }

    public void testDateAnonymizer() {

        assertEquals(new Date(0), new Date(0));

        List<Object> fromList = new ArrayList<>();
        fromList.add(new Date(0));
        fromList.add(new Date(86400000L));
        fromList.add(new Date(172800000L));

        String type = Date.class.getName();

        testAnonymizer(fromList, type, "DATE");
    }

    public void testParameterizedAnonymizer() {
        List<Object> fromList = new ArrayList<>();
        fromList.add("String 1");
        fromList.add("String 2");
        fromList.add("String 3");

        String type = new FixedValueAnonymizer().getType();

        testAnonymizer(fromList, type, type);
    }

    public void testEmailAddressAnonymizerHandlesPostgreSqlCitextObject() throws Exception {
        Column column = new Column("email", "EMAIL_ADDRESS", 100, false);
        Object citextValue = createPostgreSqlObject("citext", "source@example.com");

        Synonym synonym = anonService.anonymize(column, citextValue);

        assertEquals("EMAIL_ADDRESS", synonym.getType());
        assertEquals("source@example.com", synonym.getFrom());
        assertTrue(synonym.getTo().toString().contains("@example.com"));
    }

    public void testStringAnonymizerHandlesPostgreSqlObject() throws Exception {
        Column column = new Column("description", "STRING", 100, false);
        Object postgreSqlValue = createPostgreSqlObject("citext", "source value");

        Synonym synonym = anonService.anonymize(column, postgreSqlValue);

        assertEquals("STRING", synonym.getType());
        assertEquals("source value", synonym.getFrom());
        assertNotNull(synonym.getTo());
    }

    private void testAnonymizer(List<Object> fromList, String lookupType, String synonymType) {
        List<Object> toList = new ArrayList<>();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("value", "testValue");

        Column column = new Column("Testcolumn", lookupType, 100, false, parameters);

        // First pass
        for (Object from : fromList) {
            Synonym s = anonService.anonymize(column, from);

            assertEquals(from, s.getFrom());
            assertEquals(synonymType, s.getType());
            assertNotNull(s.getTo());

            toList.add(s.getTo());
        }

        // Second pass (consistency check)
        for (int i = 0; i < fromList.size(); i++) {
            Synonym s = anonService.anonymize(column, fromList.get(i));
            assertEquals(toList.get(i), s.getTo());
        }

        // Test passing in null
        assertNull(anonService.anonymize(column, null).getTo());
    }

    private Object createPostgreSqlObject(String type, String value) throws Exception {
        File postgresqlDriver = new File("resources/libraries/postgresql-9.0-801.jdbc4.jar");
        URLClassLoader classLoader = new URLClassLoader(new URL[]{postgresqlDriver.toURI().toURL()});
        Class<?> pgObjectClass = classLoader.loadClass("org.postgresql.util.PGobject");
        Object pgObject = pgObjectClass.newInstance();
        Method setType = pgObjectClass.getMethod("setType", String.class);
        Method setValue = pgObjectClass.getMethod("setValue", String.class);
        setType.invoke(pgObject, type);
        setValue.invoke(pgObject, value);
        return pgObject;
    }
}
