package com.rolfje.anonimatron.anonymizer;


public abstract class AbstractElevenProofAnonymizer implements Anonymizer {

    protected int[] add(int[] digits, int number) {
        String numberString = digitsAsNumber(digits);
        int intValue = Integer.parseInt(numberString) + number;
        return numberAsDigits(intValue, digits.length);
    }


    /**
     * @deprecated Use the more correctly named {@link #digitsAsString(int[])}
     */
    @Deprecated
    protected String digitsAsNumber(int[] digits) {
        return digitsAsString(digits);
    }

    protected String digitsAsString(int[] digits) {
        String result = "";
        for (int i : digits) {
            result += String.valueOf(i);
        }
        return result;
    }

    protected int digitsAsInteger(int[] digits) {
        int result = 0;
        for (int i = 0; i < digits.length; i++) {
            result += (int) Math.pow(10, (digits.length - i - 1)) * digits[i];
        }
        return result;
    }

    private int[] numberAsDigits(int number, int digits) {
        String s = String.valueOf(number);

        while (s.length() < digits) {
            s = "0" + s;
        }

        int[] digitarray = new int[digits];
        for (int i = 0; i < digitarray.length; i++) {
            digitarray[i] = Integer.parseInt("" + s.charAt(i));
        }

        return digitarray;
    }

    /**
     * For the given digits in the array, calculate the
     * seperate 11-proof values.
     *
     * @param elevennumber an account, sofi or bsn number, with bsnnumber[0] being the
     *                     most significant digit.
     * @return An array of equal size, where each number represents the 11 proof
     * result for the corresponding digit in the bsnnumber paramter.
     */
    protected int[] calculate11proofdigits(int[] elevennumber) {
        int[] elevenValues = new int[elevennumber.length];
        for (int i = 0; i < elevennumber.length; i++) {
            elevenValues[i] = elevennumber[i] * (elevennumber.length - i);
        }
        return elevenValues;
    }

    protected int sum(int[] digits) {
        int sum = 0;
        for (int i : digits) {
            sum += i;
        }
        return sum;
    }

    protected int[] generate11ProofNumber(int numberOfDigits) {
        int[] elevenProof = getRandomDigits(numberOfDigits);
        int sum11values = sum(calculate11proofdigits(elevenProof));
        int correctedSum = Math.round((float) sum11values / 11) * 11;
        int correction = correctedSum < sum11values ? -1 : +1;
        do {
            // Correct number and see if we corrected completely.
            elevenProof = add(elevenProof, correction);
        } while (sum(calculate11proofdigits(elevenProof)) != correctedSum);
        return elevenProof;
    }

    protected int[] getRandomDigits(int numberOfDigits) {
        int[] randomDigits = new int[numberOfDigits];
        for (int i = 0; i < randomDigits.length; i++) {
            randomDigits[i] = (int) Math.floor(Math.random() * 10);
        }
        return randomDigits;
    }
}
