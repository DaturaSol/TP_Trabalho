package trabalho.financeiro.utils;

import java.util.InputMismatchException;

public final class CpfCnpjManager {

    // Private constructor to prevent instantiation of this utility class
    private CpfCnpjManager() {
    }

    private static final int CPF_LENGTH = 11;
    private static final int CNPJ_LENGTH = 14;

    // Regex to find any character that is not a digit
    private static final String NOT_A_DIGIT = "\\D";

    /**
     * Removes all non-digit characters from a string.
     * Ideal for storing the document number in a database.
     *
     * @param doc The CPF or CNPJ string.
     * @return A string containing only the numbers from the input. Returns an empty
     *         string if input is null.
     */
    public static String toOnlyNumbers(String doc) {
        if (doc == null) {
            return ""; // Or return null, depending on your preference
        }
        return doc.replaceAll(NOT_A_DIGIT, "");
    }

    /**
     * Validates if the given string is a valid CPF or CNPJ.
     *
     * @param doc The CPF or CNPJ to validate, can be formatted or numbers-only.
     * @return true if the document is a valid CPF or CNPJ, false otherwise.
     */
    public static boolean isValid(String doc) {
        String cleanDoc = toOnlyNumbers(doc);

        if (cleanDoc.isEmpty()) {
            return false;
        }

        if (cleanDoc.length() == CPF_LENGTH) {
            return isValidCPF(cleanDoc);
        } else if (cleanDoc.length() == CNPJ_LENGTH) {
            return isValidCNPJ(cleanDoc);
        }

        return false;
    }

    /**
     * Formats a numbers-only CPF or CNPJ string into the standard display format.
     * CPF: ###.###.###-##
     * CNPJ: ##.###.###/####-##
     *
     * @param doc The CPF or CNPJ string, can be formatted or numbers-only.
     * @return The formatted string, or the original string if it's not a valid
     *         length.
     */
    public static String format(String doc) {
        String cleanDoc = toOnlyNumbers(doc);

        if (cleanDoc.length() == CPF_LENGTH) {
            return cleanDoc.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (cleanDoc.length() == CNPJ_LENGTH) {
            return cleanDoc.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }

        // Return the cleaned (or original if no numbers) string if it has an invalid
        // length
        return doc;
    }

    private static boolean isValidCPF(String cpf) {
        // Check for sequences of same digits (e.g., 11111111111), which are invalid
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            // Calculation of the 1st Check Digit
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (cpf.charAt(i) - '0');
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char) (r + '0');

            // Calculation of the 2nd Check Digit
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (cpf.charAt(i) - '0');
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char) (r + '0');

            // Check if the calculated digits match the given digits
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (InputMismatchException erro) {
            return false;
        }
    }

    private static boolean isValidCNPJ(String cnpj) {
        // Check for sequences of same digits (e.g., 11111111111111), which are invalid
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        char dig13, dig14;
        int sm, i, r, num, peso;

        try {
            // Calculation of the 1st Check Digit
            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                num = (cnpj.charAt(i) - '0');
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else
                dig13 = (char) ((11 - r) + '0');

            // Calculation of the 2nd Check Digit
            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (cnpj.charAt(i) - '0');
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else
                dig14 = (char) ((11 - r) + '0');

            // Check if the calculated digits match the given digits
            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
        } catch (InputMismatchException erro) {
            return false;
        }
    }
}