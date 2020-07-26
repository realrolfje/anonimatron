package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

import java.util.Map;

public class DigitStringAnonymizer extends AbstractElevenProofAnonymizer {

    final static String PARAMETER = "mask";

    @Override
    public String getType() {
        return "RANDOMDIGITS";
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived, Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return anonymize(from, size, shortlived);
        } else if (!parameters.containsKey(PARAMETER)) {
            throw new UnsupportedOperationException("Please provide '" + PARAMETER + "' with a digit mask in the form 111******, where only stars are replaced with random characters.");
        }
        return anonymizeMasked(from, size, shortlived, parameters.get(PARAMETER));
    }

    @Override
    public Synonym anonymize(Object from, int size, boolean shortlived) {
        if (from == null) {
            return new StringSynonym(
                    getType(),
                    null,
                    null,
                    shortlived
            );
        }

        int length = from.toString().length();

        int[] digits = getRandomDigits(length);
        String to = digitsAsString(digits);

        return new StringSynonym(
                getType(),
                (String) from,
                to,
                shortlived
        );
    }

    /**
     * Anonymizes a string with digits into a new string of digits where only a part
     * of the original digits are changed. You can use this to mask out a certain part
     * of the original string, like an area code of a phone number, or the bank number
     * of a credit card number.
     * <p>
     * A mask looks like "111***", where digits remain original, and stars (or other
     * characters) are replaced with random digits. An example:
     * <p>
     * Original number: 555-1234
     * Mask:            1111**** (note that the '-' is also untouched by placing a digit there)
     * New number     : 555-9876
     * <p>
     * Note that if the mask is shorter than the input string, the output string will
     * have random digits where there is no mask. In short: only the locations where
     * there is a digit in the mask are left untouched and will be in the output.
     *
     * @param from
     * @param size
     * @param shortlived
     * @param mask
     * @return
     */
    private Synonym anonymizeMasked(Object from, int size, boolean shortlived, String mask) {
        if (from == null) {
            return new StringSynonym(
                    getType(),
                    null,
                    null,
                    shortlived
            );
        }

        char[] fromChars = from.toString().toCharArray();
        char[] toChars = new char[fromChars.length];

        for (int i = 0; i < fromChars.length; i++) {
            if (i < mask.length() && Character.isDigit(mask.charAt(i))) {
                toChars[i] = fromChars[i];
            } else {
                toChars[i] = Character.forDigit((int) Math.floor(Math.random() * 10), 10);
            }
        }

        return new StringSynonym(
                getType(),
                String.valueOf(fromChars),
                String.valueOf(toChars),
                shortlived
        );
    }
}
