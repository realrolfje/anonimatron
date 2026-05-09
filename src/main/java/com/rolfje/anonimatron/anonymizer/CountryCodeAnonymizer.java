package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * Produces valid 2 or 3 character country codes.
 */
public class CountryCodeAnonymizer implements Anonymizer {

	private static final String TYPE = "COUNTRY_CODE";
	protected static final String[] ISO_COUNTRIES = Locale.getISOCountries();
	SecureRandom r = new SecureRandom();

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Synonym anonymize(Object from, int size, boolean shortlived) {

		if (size < 2) {
			throw new UnsupportedOperationException("Can not produce country codes of one character.");
		}

		String country = null;
		while (country == null || country.length() < 1) {
			country = ISO_COUNTRIES[r.nextInt(ISO_COUNTRIES.length)];
			Locale l = new Locale("", country);

			try {
				if (size > 2) {
					country = l.getISO3Country();
					country = padRight(country, size);
				}
			} catch (MissingResourceException e) {
				// Locale.getISOCountries() has inconsistent behaviour for "AN", "BU" and "CS" country codes
				// See https://bugs.openjdk.java.net/browse/JDK-8071929
				country = null;
			}
		}

		return new StringSynonym(
				getType(),
				from.toString(),
				country,
				shortlived
		);
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}
}
