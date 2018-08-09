package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.log4j.Logger;

import java.security.SecureRandom;

/**
 * Generates valid Dutch Bank Account number with the same length as the given
 * number.
 * <p>
 * A Dutch Bank Account Number is passes the "11 proof" if it is 9 digits long.
 * If it is less than 9 digits, there is no way to check the validity of the
 * number.
 * <p>
 * See http://nl.wikipedia.org/wiki/Elfproef
 */
public class DutchBankAccountAnononymizer extends AbstractElevenProofAnonymizer implements BankAccountAnonymizer {
    private static Logger LOG = Logger.getLogger(DutchBankAccountAnononymizer.class);

    private static int LENGTH = 9;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String getType() {
        return "DUTCHBANKACCOUNT";
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        if (size < LENGTH) {
            throw new UnsupportedOperationException(
                    "Can not generate a Dutch Bank Account number that fits in a " + size + " character string. Must be " + LENGTH
                            + " characters or more.");
        }

        String fromString = (String) from;

        int originalLength = (fromString).length();
        if (originalLength > LENGTH) {
            LOG.warn("Original bank account number had more than " + LENGTH
                    + " digits. The resulting anonymous bank account number with the same length will not be a valid account number.");

        }

        String toString = fromString;
        do {
            toString = generateBankAccount(originalLength);
        } while (fromString.equals(toString));

        return new StringSynonym(
                getType(),
                fromString,
                toString,
                shortlived
        );
    }

    @Override
    public String generateBankAccount(int numberOfDigits) {
        if (numberOfDigits == LENGTH) {
            // Generate 11-proof bank account number
            int[] elevenProof = generate11ProofNumber(numberOfDigits);
            return digitsAsNumber(elevenProof);
        } else {
            // Generate non-11 proof bank account number
            int[] randomnumber = getRandomDigits(numberOfDigits);
            return digitsAsNumber(randomnumber);
        }
    }

    @Override
    public String generateBankCode() {
        DutchBankCode bankCode = DutchBankCode.values()[random.nextInt(DutchBankCode.values().length)];

        return bankCode.bankCode;
    }

    /**
     * Enumeration of the Dutch bank codes, used to generate valid IBAN for Dutch bank accounts.
     *
     * @see <a href="Bank Identifier Codes">https://www.betaalvereniging.nl/aandachtsgebieden/giraal-betalingsverkeer/bic-sepa-transacties/</a>
     */
    private enum DutchBankCode {

        ABNA("ABNANL2A", "ABNA", "ABN AMRO"),

        FTSB("ABNANL2A", "FTSB", "ABN AMRO (ex FORTIS)"),

        ADYB("ADYBNL2A", "ADYB", "ADYEN"),

        AEGO("AEGONL2U", "AEGO", "AEGON BANK"),

        ANAA("ANAANL21", "ANAA", "BRAND NEW DAY (ex ALLIANZ)"),

        ANDL("ANDLNL2A", "ANDL", "ANADOLUBANK"),

        ARBN("ARBNNL22", "ARBN", "ACHMEA BANK"),

        ARSN("ARSNNL21", "ARSN", "ARGENTA SPAARBANK"),

        ASNB("ASNBNL21", "ASNB", "ASN BANK"),

        ATBA("ATBANL2A", "ATBA", "AMSTERDAM TRADE BANK"),

        BCDM("BCDMNL22", "BCDM", "BANQUE CHAABI DU MAROC"),

        BCIT("BCITNL2A", "BCIT", "INTESA SANPAOLO"),

        BICK("BICKNL2A", "BICK", "BINCKBANK"),

        BINK("BINKNL21", "BINK", "BINCKBANK, PROF"),

        BKCH("BKCHNL2R", "BKCH", "BANK OF CHINA"),

        BKMG("BKMGNL2A", "BKMG", "BANK MENDES GANS"),

        BLGW("BLGWNL21", "BLGW", "BLG WONEN"),

        BMEU("BMEUNL21", "BMEU", "BMCE EUROSERVICES"),

        BNDA("BNDANL2A", "BNDA", "BRAND NEW DAY BANK"),

        BNGH("BNGHNL2G", "BNGH", "BANK NEDERLANDSE GEMEENTEN"),

        BNPA("BNPANL2A", "BNPA", "BNP PARIBAS"),

        BOFA("BOFANLNX", "BOFA", "BANK OF AMERICA"),

        BOFS("BOFSNL21002", "BOFS", "BANK OF SCOTLAND, AMSTERDAM"),

        BOTK("BOTKNL2X", "BOTK", "MUFG BANK"),

        BUNQ("BUNQNL2A", "BUNQ", "BUNQ"),

        CHAS("CHASNL2X", "CHAS", "JPMORGAN CHASE"),

        CITC("CITCNL2A", "CITC", "CITCO BANK"),

        CITI("CITINL2X", "CITI", "CITIBANK INTERNATIONAL"),

        COBA("COBANL2X", "COBA", "COMMERZBANK"),

        DEUT("DEUTNL2A", "DEUT", "DEUTSCHE BANK (bij alle SEPA transacties)"),

        DHBN("DHBNNL2R", "DHBN", "DEMIR-HALK BANK"),

        DLBK("DLBKNL2A", "DLBK", "DELTA LLOYD BANK"),

        DNIB("DNIBNL2G", "DNIB", "NIBC"),

        EBUR("EBURNL21", "EBUR", "EBURY NETHERLANDS"),

        FBHL("FBHLNL2A", "FBHL", "CREDIT EUROPE BANK"),

        FLOR("FLORNL2A", "FLOR", "DE NEDERLANDSCHE BANK"),

        FRGH("FRGHNL21", "FRGH", "FGH BANK"),

        FRNX("FRNXNL2A", "FRNX", "FRANX"),

        FVLB("FVLBNL22", "FVLB", "VAN LANSCHOT"),

        GILL("GILLNL2A", "GILL", "INSINGERGILISSEN"),

        HAND("HANDNL2A", "HAND", "SVENSKA HANDELSBANKEN"),

        HHBA("HHBANL22", "HHBA", "HOF HOORNEMAN BANKIERS"),

        HSBC("HSBCNL2A", "HSBC", "HSBC BANK"),

        ICBK("ICBKNL2A", "ICBK", "INDUSTRIAL & COMMERCIAL BANK OF CHINA"),

        INGB("INGBNL2A", "INGB", "ING"),

        ISAE("ISAENL2A", "ISAE", "CACEIS BANK, Netherlands Branch"),

        ISBK("ISBKNL2A", "ISBK", "ISBANK"),

        KABA("KABANL2A", "KABA", "YAPI KREDI BANK"),

        KASA("KASANL2A", "KASA", "KAS BANK"),

        KNAB("KNABNL2H", "KNAB", "KNAB"),

        KOEX("KOEXNL2A", "KOEX", "KOREA EXCHANGE BANK"),

        KRED("KREDNL2X", "KRED", "KBC BANK"),

        LOCY("LOCYNL2A", "LOCY", "LOMBARD ODIER DARIER HENTSCH & CIE"),

        LOYD("LOYDNL2A", "LOYD", "LLOYDS TSB BANK"),

        LPLN("LPLNNL2F", "LPLN", "LEASEPLAN CORPORATION"),

        MHCB("MHCBNL2A", "MHCB", "MIZUHO BANK EUROPE"),

        MOYO("MOYONL21", "MOYO", "MONEYOU"),

        NNBA("NNBANL2G", "NNBA", "NATIONALE-NEDERLANDEN BANK"),

        NWAB("NWABNL2G", "NWAB", "NEDERLANDSE WATERSCHAPSBANK"),

        PCBC("PCBCNL2A", "PCBC", "CHINA CONSTRUCTION BANK, AMSTERDAM BRANCH"),

        RABO("RABONL2U", "RABO", "RABOBANK"),

        RBRB("RBRBNL21", "RBRB", "REGIOBANK"),

        SOGE("SOGENL2A", "SOGE", "SOCIETE GENERALE"),

        TEBU("TEBUNL2A", "TEBU", "THE ECONOMY BANK"),

        TRIO("TRIONL2U", "TRIO", "TRIODOS BANK"),

        UBSW("UBSWNL2A", "UBSW", "UBS EUROPE SE, NETHERLANDS BRANCH"),

        UGBI("UGBINL2A", "UGBI", "GARANTIBANK INTERNATIONAL"),

        VOWA("VOWANL21", "VOWA", "VOLKSWAGEN BANK"),

        ZWLB("ZWLBNL21", "ZWLB", "SNS (ex ZWITSERLEVENBANK)");

        private final String bic;
        private final String bankCode;
        private final String bankNaam;

        DutchBankCode(String bic, String bankCode, String bankNaam) {
            this.bic = bic;
            this.bankCode = bankCode;
            this.bankNaam = bankNaam;
        }
    }
}
