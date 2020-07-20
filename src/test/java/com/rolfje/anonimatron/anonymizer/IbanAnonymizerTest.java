package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import com.rolfje.junit.Asserts;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.bban.BbanStructure;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
            String from = anonymizer.generateIban(countryCode);

            Asserts.assertAnyOf(Iban.valueOf(from).getCountryCode(),
                    countryCode,
                    IbanAnonymizer.DEFAULT_COUNTRY_CODE
            );

            testInternal(from.length(), from, countryCode);
        }
    }

    private void testInternal(int size, String from, CountryCode countryCode) {
        Synonym synonym = anonymizer.anonymize(from, size, false);
        assertEquals(anonymizer.getType(), synonym.getType());

        Asserts.assertAnyOf(Iban.valueOf((String) synonym.getTo()).getCountryCode(),
                countryCode,
                IbanAnonymizer.DEFAULT_COUNTRY_CODE
        );
    }
}
