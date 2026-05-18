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

    private Duration interval;
    private LocalDateTime nextNotificationAt;

    private boolean isEnabled;
    private boolean notifyWhenVacanciesNotFound;

    public void setInterval(Duration interval) {
        this.interval = interval;
        scheduleNext();
    }

    public boolean isTimeToNotify() {
        return nextNotificationAt != null
                && LocalDateTime.now().isAfter(nextNotificationAt);
    }

    public void scheduleNext() {
        if (interval != null) {
            this.nextNotificationAt = LocalDateTime.now().plus(interval);
        }
    }

    public void disable() {
        interval = null;
        nextNotificationAt = null;
        isEnabled = false;
    }
}
