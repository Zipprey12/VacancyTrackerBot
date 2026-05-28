package vacancy_tracker.model.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {

    private long chatId;
    private Duration interval;
    private LocalDateTime nextNotificationAt;
    private LocalDateTime lastNotificationAt;

    private boolean isEnabled;
    private boolean notifyWhenVacanciesNotFound;

    public boolean isTimeToNotify() {
        return nextNotificationAt != null
                && LocalDateTime.now().isAfter(nextNotificationAt);
    }

    public void scheduleNext() {
        if (interval != null) {
            var now = LocalDateTime.now();
            if (nextNotificationAt == null) {
                nextNotificationAt = now.plus(interval);
            } else {
                lastNotificationAt = nextNotificationAt;
                var next = nextNotificationAt.plus(interval);
                if (next.isBefore(now)) {
                    nextNotificationAt = now.plus(interval);
                } else nextNotificationAt = next;
            }
        }
    }
}