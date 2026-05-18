package vacancy_tracker.services;

import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class StringUtil {

    public static Optional<Integer> parseInt(String text) {
        if (text == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Float> parseFloat(String text) {
        if (text == null) {
            return Optional.empty();
        }
        try {
            String normalized = text.replace(',', '.');
            return Optional.of(Float.parseFloat(normalized));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Boolean> parseBoolean(String text) {
        if (text == null) {
            return Optional.empty();
        }

        String trimmed = text.trim().toLowerCase();
        if ("true".equals(trimmed)) {
            return Optional.of(true);
        }
        if ("false".equals(trimmed)) {
            return Optional.of(false);
        }
        return Optional.empty();
    }
}
