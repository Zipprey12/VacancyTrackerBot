package vacancy_tracker.services.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class StringUtil {

    public static Optional<Integer> parseInt(String text) {
        return parseNumber(text, Integer::parseInt);
    }

    public static Optional<Long> parseLong(String text) {
        return parseNumber(text, Long::parseLong);
    }

    public static Optional<Float> parseFloat(String text) {
        if (text == null) return Optional.empty();
        return parseNumber(text.replace(',', '.'), Float::parseFloat);
    }

    public static Optional<Boolean> parseBoolean(String text) {
        if (text == null) return Optional.empty();
        return switch (text.trim().toLowerCase()) {
            case "true" -> Optional.of(true);
            case "false" -> Optional.of(false);
            default -> Optional.empty();
        };
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

    private static <T> Optional<T> parseNumber(String text, Function<String, T> parser) {
        if (text == null) return Optional.empty();
        var prepared = text.replace(" ", "");
        try {
            return Optional.of(parser.apply(prepared));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
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
