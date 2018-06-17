package com.rolfje.anonimatron.anonymizer;

/**
 * An {@linkplain Anonymizer} for Bank Accounts.
 *
 * @author Erik-Berndt Scheper
 */
interface BankAccountAnonymizer extends Anonymizer {

	/**
	 * Generate a bank account using the given number of digits.
	 *
	 * @param numberOfDigits the number of digits in the account number.
	 * @return the bank account
	 */
	String generateBankAccount(int numberOfDigits);

	/**
	 * Generate a valid bank code.
	 *
	 * @return a valid bank code
	 */
	String generateBankCode();

}
