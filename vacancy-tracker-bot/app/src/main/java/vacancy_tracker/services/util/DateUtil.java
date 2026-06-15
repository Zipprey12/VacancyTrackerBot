package vacancy_tracker.services.util;

import lombok.experimental.UtilityClass;

import java.time.*;

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

    public static long toUnixSeconds(LocalDateTime dateTime) {
        if (dateTime == null) return 0;
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static LocalDateTime fromUnixSeconds(long unixSeconds) {
        return Instant.ofEpochSecond(unixSeconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDateTime nextTimeWithInterval(LocalDateTime from, LocalTime newTime,
                                                     Duration interval) {
        var candidate = from.with(newTime);

        if (candidate.isBefore(LocalDateTime.now())) {
            long intervalSeconds = interval.toSeconds();
            long secondsBehind = Duration.between(candidate, LocalDateTime.now()).toSeconds();
            long intervalsToAdd = (secondsBehind / intervalSeconds) + 1;
            candidate = candidate.plus(interval.multipliedBy(intervalsToAdd));
        }

        return candidate;
    }
}
