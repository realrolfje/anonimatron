package com.rolfje.anonimatron.anonymizer;

import org.castor.core.util.Base64Encoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;


public class Hasher {

	// Ideally more iterations mean better protection against brute
	// force attacks. Since synonyms are accross the while dataset,
	// an attacker would only know the generated synonym type based
	// on the synonym file, but not the source date type or location.
	private static final int ITERATIONS = 64;

	// Roughly same space as version 4 UUID, more than 5 undecillion combinations,
	// very unlikely to generate collisions.
	private static final int SIZE = 128;

	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
	public static final Charset CHARSET = Charset.forName("UTF-8");
	private byte[] salt;

	public Hasher(String salt) {
		this.salt = salt.getBytes();
	}

	public String base64Hash(Object object) {
		byte[] serialize = serialize(object);
		char[] chars = toCharArray(serialize);
		return new String(Base64Encoder.encode(pbkdf2(chars)));
	}

	public String base64Hash(String object) {
		byte[] hash = pbkdf2(object.toCharArray());
		return new String(Base64Encoder.encode(hash));
	}

	private char[] toCharArray(byte[] bytes) {
		return new String(bytes, CHARSET).toCharArray();
	}

	private byte[] serialize(Object object) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Unexpected problem serializing object.", e);
		}
	}

	private byte[] pbkdf2(char[] password) {
		KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, SIZE);
		try {
			SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
			return f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
		} catch (InvalidKeySpecException ex) {
			throw new IllegalStateException("Invalid SecretKeyFactory", ex);
		}
	}

}
