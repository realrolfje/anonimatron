# Available Anonymizers

Anonimatron comes with the following default anonymizers. Please start Anonimatron with the `-configexample`
parameter to see how these are configured. For more information on how Anonimatron works and runs, check our [quickstart](index.md). 

| Name                              | Type                | Input            | Output                                                                   |
|:----------------------------------|:--------------------|:-----------------|:-------------------------------------------------------------------------|
| CharacterStringAnonymizer         | RANDOMCHARACTERS    | Any string       | A-Z, same length                                                         |
| CharacterStringPrefetchAnonymizer | RANDOMCHARACTERS    | Any string       | Characters from all input data, same length                              |
| CountryCodeAnonymizer             | COUNTRY_CODE        | Any string       | ISO 3166-1 alpha 2 or alpha 3 code                                       |
| DateAnonymizer                    | DATE                | Valid date       | Date between 31 days before and 32 days after the input date             |
| DigitStringAnonymizer             | RANDOMDIGITS        | Any string       | 0-9, same length, optional mask                                          | 
| DutchBankAccountAnononymizer      | DUTCHBANKACCOUNT    | Any string       | 11 proof number, minimal 9 digits                                        | 
| DutchBSNAnononymizer              | BURGERSERVICENUMMER | Number or string | Valid Dutch "Burger Service Nummer" or "SOFI Nummer" as number or string |
| DutchZipCodeAnonymizer            | DUTCH_ZIP_CODE      | Any string       | Valid Dutch zip/postal code                                              |
| ElvenNameGenerator                | ELVEN_NAME          | Any string       | Pronounceable elven name, 2 to 5 syllables                               | 
| EmailAddressAnonymizer            | EMAIL_ADDRESS       | Any string       | Valid email address in the domain "@example.com"                         |
| IbanAnonymizer                    | IBAN                | Any string       | Valid International Bank Account Number                                  |
| RomanNameGenerator                | ROMAN_NAME          | Any string       | Pronounceable Roman name, 2 to 5 syllables                               | 
| StringAnonymizer                  | STRING              | Any string       | Random hexadecimal string                                                |
| UkPostCodeAnonymizer              | UK_POST_CODE        | Any string       | Valid Uk Post code                                                       |
| UUIDAnonymizer                    | UUID                | Any string       | A random UUID                                                            |