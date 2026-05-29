package util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern LETTERS_SPACES_PATTERN = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$");
    private static final Pattern ALPHANUMERIC_DASH_PATTERN = Pattern.compile("^[A-Za-z0-9-]+$");

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String value) {
        return !isBlank(value) && EMAIL_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isLettersAndSpaces(String value) {
        return !isBlank(value) && LETTERS_SPACES_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isNumericInRange(String value, int minLength, int maxLength) {
        if (isBlank(value)) return false;
        String text = value.trim();
        if (!text.matches("\\d+")) return false;
        return text.length() >= minLength && text.length() <= maxLength;
    }

    public static boolean isPhoneValid(String value) {
        return isNumericInRange(value, 7, 15);
    }

    public static boolean isAlphanumericDash(String value, int minLength, int maxLength) {
        if (isBlank(value)) return false;
        String text = value.trim();
        if (text.length() < minLength || text.length() > maxLength) return false;
        return ALPHANUMERIC_DASH_PATTERN.matcher(text).matches();
    }

    public static boolean isDateRangeValid(LocalDate start, LocalDate end) {
        return start != null && end != null && !end.isBefore(start);
    }

    public static boolean isPastOrToday(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }

    public static boolean isAdult(LocalDate birthDate) {
        return birthDate != null && birthDate.plusYears(18).isBefore(LocalDate.now().plusDays(1));
    }

    public static Integer parsePositiveInt(String value) {
        try {
            int number = Integer.parseInt(value.trim());
            return number > 0 ? number : null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Integer parseNonNegativeInt(String value) {
        try {
            int number = Integer.parseInt(value.trim());
            return number >= 0 ? number : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
