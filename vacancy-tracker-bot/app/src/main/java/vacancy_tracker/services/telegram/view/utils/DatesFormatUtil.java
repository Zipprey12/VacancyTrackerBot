package vacancy_tracker.services.telegram.view.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DatesFormatUtil {

    public static String formatYears(int years) {
        return years + " " + getDeclension(years);
    }

    public static String getDeclension(int years) {
        return getDeclensionForLong(years);
    }

    public static String formatYears(double years) {
        if (years == (long) years) {
            return formatYears((int) years);
        }

        String formattedNumber = formatDecimal(years);
        String declension = getDeclension(years);
        return formattedNumber + " " + declension;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public static String getDeclension(double years) {
        long integerPart = (long) years;
        double fractionalPart = years - integerPart;

        if (fractionalPart == 0) {
            return getDeclensionForLong(integerPart);
        }

        if (fractionalPart > 0 && fractionalPart < 0.5) {
            return "года";
        } else {
            return "лет";
        }
    }

    private static String getDeclensionForLong(long years) {
        int lastDigit = (int) (years % 10);
        int lastTwoDigits = (int) (years % 100);

        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return "лет";
        }

        return switch (lastDigit) {
            case 1 -> "год";
            case 2, 3, 4 -> "года";
            default -> "лет";
        };
    }

    private static String formatDecimal(double number) {
        String formatted = String.format("%.2f", number);

        formatted = formatted.replaceAll("0*$", "")
                .replaceAll("\\.$", "");

        return formatted;
    }
}
