package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

/**
 * Produces valid 2 or 3 character country codes.
 */
public class CountryCodeAnonymizer implements Anonymizer {

	private static String TYPE = "COUNTRY_CODE";
	public static final Locale[] AVAILABLE_LOCALES = SimpleDateFormat.getAvailableLocales();

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Synonym anonymize(Object from, int size) {
		Random r = new Random();

		String country = "";
		// Some Locales apparently do not have a valid country entry
		while (country == null || country.length() < 1) {
			Locale l = AVAILABLE_LOCALES[r.nextInt(AVAILABLE_LOCALES.length)];

			if (size > 2) {
				country = l.getISO3Country();
				country = padRight(country, size);
			}
			else if (size == 2) {
				country = l.getCountry();
			}
			else {
				throw new UnsupportedOperationException("Can not produce country codes of one character.");
			}
		}

		return new StringSynonym(
				getType(),
				from.toString(),
				country
		);


	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

}
