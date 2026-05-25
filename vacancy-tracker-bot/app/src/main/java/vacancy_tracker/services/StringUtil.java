package vacancy_tracker.services;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalTime;
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

    public static Optional<LocalTime> parseTime(String text) {
        return parseTimeParts(text)
                .filter(parts -> parts[0] <= 23)
                .map(parts -> LocalTime.of((int) parts[0], (int) parts[1], (int) parts[2]));
    }

    public static Optional<Duration> parseDuration(String text) {
        return parseTimeParts(text)
                .map(parts -> Duration.ofHours(parts[0])
                        .plusMinutes(parts[1])
                        .plusSeconds(parts[2]));
    }

    private static Optional<long[]> parseTimeParts(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        var normalized = text.trim().replace(':', ' ');
        var parts = normalized.split("\\s+");

        try {
            long hours = Long.parseLong(parts[0]);
            long minutes = parts.length > 1 ? Long.parseLong(parts[1]) : 0;
            long seconds = parts.length > 2 ? Long.parseLong(parts[2]) : 0;

            if (hours < 0 || minutes < 0 || seconds < 0 || minutes > 59 || seconds > 59) {
                return Optional.empty();
            }

            return Optional.of(new long[]{hours, minutes, seconds});
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
