package vacancy_tracker.services;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@UtilityClass
public class DateUtil {

    public static final long ONE_DAY_SECONDS = 24 * 60 * 60L;
    public static final long ONE_WEEK_SECONDS = 7 * ONE_DAY_SECONDS;

    public static LocalDateTime nextDayOfWeek(LocalDateTime from, int dayOfWeek, LocalTime time) {
        int currentDay = from.getDayOfWeek().getValue();
        int daysUntil = (dayOfWeek - currentDay + 7) % 7;

        if (daysUntil == 0 && from.toLocalTime().isAfter(time)) {
            daysUntil = 7;
        }

        return from.plusDays(daysUntil).with(time);
    }

    public static LocalDateTime nextTime(LocalDateTime from, LocalTime time) {
        var current = from.toLocalDate().atTime(time);

        if (current.isAfter(from) || current.equals(from)) {
            return current;
        }
        return current.plusDays(1);
    }

    public static LocalDateTime withTime(LocalDateTime dateTime, LocalTime time) {
        return dateTime.toLocalDate().atTime(time);
    }

    public static long toUnixSeconds(LocalDateTime dateTime) {
        if (dateTime == null) return 0;
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static LocalDateTime fromUnixSeconds(long unixSeconds) {
        return Instant.ofEpochSecond(unixSeconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
