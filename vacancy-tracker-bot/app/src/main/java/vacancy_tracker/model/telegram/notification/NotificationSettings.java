package vacancy_tracker.model.telegram.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationSettings implements Serializable {

    private long chatId;
    private Duration interval;
    private LocalDateTime nextNotificationAt;
    private LocalDateTime lastNotificationAt;
    private Instant updatedAt;

    private boolean isEnabled;
    private boolean notifyWhenVacanciesNotFound;

    public void scheduleNext() {
        if (interval != null) {
            var now = LocalDateTime.now();
            if (nextNotificationAt == null) {
                nextNotificationAt = now.plus(interval);
            } else {
                lastNotificationAt = nextNotificationAt;
                nextNotificationAt = calculateNext(now, nextNotificationAt);
            }
        }
    }

    public void rescheduleNext() {
        if (interval != null) {
            var now = LocalDateTime.now();
            if (nextNotificationAt == null) {
                nextNotificationAt = now.plus(interval);
            } else {
                nextNotificationAt = calculateNext(now, nextNotificationAt);
            }
        }
    }

    private LocalDateTime calculateNext(LocalDateTime now, LocalDateTime current) {
        var next = current.plus(interval);
        return next.isBefore(now) ? now.plus(interval) : next;
    }
}