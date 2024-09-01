package com.rolfje.anonimatron.anonymizer;

import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;

import static org.junit.Assert.assertNotEquals;

public class HasherTest extends TestCase {
    private final Logger LOG = LogManager.getLogger(HasherTest.class);

    public void testBase64HashHappy() {
        assertEquals(
                new Hasher("salt").base64Hash("piep"),
                new Hasher("salt").base64Hash("piep")
        );
    }

    public void testBase64HashHappyDate() {
        assertEquals(
                new Hasher("salt").base64Hash(new Date(12345)),
                new Hasher("salt").base64Hash(new Date(12345))
        );
    }

    public void testWrongSalt() {
        String input = "piep";
        String salt1 = new Hasher("salt1").base64Hash(input);
        String salt2 = new Hasher("salt2").base64Hash(input);

        assertNotEquals(salt1, salt2);
    }

    public void testWrongInput() {
        String salt = "piep";
        String input1 = new Hasher(salt).base64Hash("piep1");
        String input2 = new Hasher(salt).base64Hash("piep2");

        assertNotEquals(input1, input2);
    }

    public void testHashSpeed() {
        long start = System.nanoTime();
        long iterations = 2000;
        long size = 0;
        for (int i = 0; i < iterations; i++) {
            size += new Hasher("salt").base64Hash("piep").length();
        }

        long durationMillis = (System.nanoTime() - start) / 1_000_000;
        LOG.info(String.format("%d hashes took %d milliseconds.", iterations, durationMillis));
        assertTrue("Hashing " + iterations + " times took " + durationMillis
                + " ms and generated " + size + " characters.", durationMillis < 1000);
    }
}