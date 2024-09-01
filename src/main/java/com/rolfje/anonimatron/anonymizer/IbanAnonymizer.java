package com.rolfje.anonimatron.anonymizer;


import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.Iban4jException;
import org.iban4j.bban.BbanStructure;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

/**
 * Generates valid International Bank Account Numbers, or {@code IBAN}'s.
 * Tries to use the {@link BankAccountAnonymizer} to generate valid {@code BBAN}'s for the system default country code.
 * If no such anonymizer exists, a random account is generated.
 * <p>
 *
 * @author Erik-Berndt Scheper
 * @see <a href="https://en.wikipedia.org/wiki/International_Bank_Account_Number">https://en.wikipedia.org/wiki/International_Bank_Account_Number</a>
 */
public class IbanAnonymizer implements Anonymizer {

	private static final Logger LOGGER = LogManager.getLogger(IbanAnonymizer.class);

	private static final String TYPE = "IBAN";

	static final Map<CountryCode, BankAccountAnonymizer> BANK_ACCOUNT_ANONYMIZERS;
	static final CountryCode DEFAULT_COUNTRY_CODE;

	static {
		// initialize bank account anonymizers
		BANK_ACCOUNT_ANONYMIZERS = new EnumMap<>(CountryCode.class);
		BANK_ACCOUNT_ANONYMIZERS.put(CountryCode.NL, new DutchBankAccountAnononymizer());

		// set a default country code to be used when the original iban cannot be parsed
		CountryCode countryCode = CountryCode.getByCode(Locale.getDefault().getCountry());

		if (countryCode != null && BbanStructure.forCountry(countryCode) != null) {
			DEFAULT_COUNTRY_CODE = countryCode;
		} else {
			DEFAULT_COUNTRY_CODE = CountryCode.NL;
		}
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Synonym anonymize(Object from, int size, boolean shortlived) {

		CountryCode countryCode = getCountryCode(from);
		String result = generateIban(countryCode);

		return new StringSynonym(
				getType(),
				(String) from,
				result,
				shortlived
		);
	}

	String generateIban(CountryCode countryCode) {
		BankAccountAnonymizer bankAccountAnonymizer = BANK_ACCOUNT_ANONYMIZERS.get(countryCode);

		String accountNumber = null;
		String bankCode = null;
		if (bankAccountAnonymizer != null) {
			accountNumber = "0" + bankAccountAnonymizer.generateBankAccount(9);
			bankCode = bankAccountAnonymizer.generateBankCode();
		}

		Iban iban = new Iban.Builder().
				countryCode(countryCode).
				accountNumber(accountNumber).
				bankCode(bankCode).
				buildRandom();

		return iban.toString();
	}

	private CountryCode getCountryCode(Object from) {
		CountryCode countryCode = DEFAULT_COUNTRY_CODE;

		if (from instanceof String) {
			try {
				Iban iban = Iban.valueOf((String) from);

				// verify this country code is supported for BBAN randomisation
				if (BbanStructure.forCountry(iban.getCountryCode()) == null) {
					countryCode = iban.getCountryCode();
				}

			} catch (Iban4jException ibex) {
				// ignore
				LOGGER.trace("Ignoring IBAN value " + from, ibex);
			}
		}

		return countryCode;
	}

}
