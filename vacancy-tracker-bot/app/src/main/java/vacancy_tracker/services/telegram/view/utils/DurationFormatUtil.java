package vacancy_tracker.services.telegram.view.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

@UtilityClass
public class DurationFormatUtil {

    private static final long MINUTE = 60;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long WEEK = 7 * DAY;
    private static final long MONTH = 30 * DAY;

    public static String format(Duration duration) {
        if (duration == null) return "не установлен";

        long seconds = duration.getSeconds();
        if (seconds == 0) return "0 мин.";

        List<String> parts = new LinkedList<>();

        long months = seconds / MONTH;
        seconds %= MONTH;

        long weeks = seconds / WEEK;
        seconds %= WEEK;

        long days = seconds / DAY;
        seconds %= DAY;

        long hours = seconds / HOUR;
        seconds %= HOUR;

        long minutes = seconds / MINUTE;

        if (months > 0) parts.add(months + " мес.");
        if (weeks > 0) parts.add(weeks + " нед.");
        if (days > 0) parts.add(days + " дн.");
        if (hours > 0) parts.add(hours + " ч.");
        if (minutes > 0) parts.add(minutes + " мин.");

        return String.join(" ", parts);
    }

}
