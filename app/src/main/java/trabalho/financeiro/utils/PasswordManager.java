package trabalho.financeiro.utils;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

/**
 * A utility class for handling password-related operations like hashing,
 * verification, and strength validation.
 * 
 * @author Gabriel M.S.O.
 */
public final class PasswordManager {

    private PasswordManager() {
        // Prevent instantiation
    }

    private static final int WORK_FACTOR = 12;

    public enum PasswordViolations {
        TOO_SHORT("Password must be at least 8 characters long."),
        NO_LETTERS("Password must contain at least one letter."),
        NO_DIGITS("Password must contain at least one number."),
        NO_SPECIAL_CHAR("Password must contain at least one special character."),
        NO_UPPERCASE("Password must contain at least one uppercase letter."),
        NO_LOWERCASE("Password must contain at least one lowercase letter.");

        private final String message;

        PasswordViolations(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Hashes a plain text password using BCrypt.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verifies a password against a stored hash.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * Validates a password against all defined strength rules, and returns a set of
     * violations.
     */
    public static Set<PasswordViolations> getPasswordViolations(String password) {
        Set<PasswordViolations> violations = EnumSet.noneOf(PasswordViolations.class);

        if (password.length() < 8 || password == null) {
            violations.add(PasswordViolations.TOO_SHORT);
        }
        if (!password.chars().anyMatch(Character::isLetter)) {
            violations.add(PasswordViolations.NO_LETTERS);
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            violations.add(PasswordViolations.NO_DIGITS);
        }
        if (!password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch))) {
            violations.add(PasswordViolations.NO_SPECIAL_CHAR);
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            violations.add(PasswordViolations.NO_UPPERCASE);
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            violations.add(PasswordViolations.NO_LOWERCASE);
        }

        return violations;
    }

    /**
     * A simple convenience method that returns true only if there are no validation
     * errors.
     */
    public static boolean isPasswordValid(String password) {
        return getPasswordViolations(password).isEmpty();
    }

    /**
     * Formats a Set of PasswordViolations into a single, multi-line string.
     *
     * @param violations The set of violations to format.
     * @return A formatted string listing all violations, or an empty string if the
     *         set is empty.
     */
    public static String formatViolations(Set<PasswordViolations> violations) {
        if (violations == null || violations.isEmpty()) {
            return "";
        }

        // Using Java 8 Streams - modern and concise
        return violations.stream()
                .map(PasswordViolations::getMessage) // Get the message string from each violation
                .collect(Collectors.joining("\n")); // Join them together with a newline character
    }
}
