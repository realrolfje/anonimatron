package com.rolfje.anonimatron.anonymizer;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.EnumSet;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.bban.BbanStructure;
import org.junit.Test;

import com.rolfje.anonimatron.synonyms.Synonym;

/**
 * Tests for {@link IbanAnonymizer}.
 *
 * @author Erik-Berndt Scheper
 */
public class IbanAnonymizerTest {

	private IbanAnonymizer anonymizer = new IbanAnonymizer();
	private EnumSet<CountryCode> countriesWithBankAccountAnonymizer = EnumSet
			.copyOf(IbanAnonymizer.BANK_ACCOUNT_ANONYMIZERS.keySet());
	private EnumSet<CountryCode> countriesWithoutBankAccountAnonymizer = EnumSet.complementOf(countriesWithBankAccountAnonymizer);

	@Test
	public void testAnonymizeForCountriesWithBankAccountAnonymizer() {
		for (CountryCode countryCode : countriesWithBankAccountAnonymizer) {
			testAnonymize(countryCode);
		}
	}

	@Test
	public void testAnonymizeForOtherCountries() {
		for (CountryCode countryCode : countriesWithoutBankAccountAnonymizer) {
			if (BbanStructure.forCountry(countryCode) != null) {
				testAnonymize(countryCode);
			}
		}
	}

	private void testAnonymize(CountryCode countryCode) {
		for (int i = 0; i < 1000; i++) {
			String source = anonymizer.generateIban(countryCode);
			assertThat(Iban.valueOf(source).getCountryCode(), anyOf(is(countryCode), is(IbanAnonymizer.DEFAULT_COUNTRY_CODE)));

			testInternal(source.length(), source, countryCode);
		}
	}

	private void testInternal(int size, String from, CountryCode countryCode) {
		Synonym synonym = anonymizer.anonymize(from, size);
		assertThat(synonym.getType(), sameInstance(anonymizer.getType()));

		String value = (String) synonym.getTo();

		assertThat(Iban.valueOf(value).getCountryCode(), anyOf(is(countryCode), is(IbanAnonymizer.DEFAULT_COUNTRY_CODE)));
	}

}
