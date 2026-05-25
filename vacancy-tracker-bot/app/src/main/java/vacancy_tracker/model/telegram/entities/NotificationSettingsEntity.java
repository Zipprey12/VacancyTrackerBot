package vacancy_tracker.model.telegram.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
public class NotificationSettingsEntity {

    @Id
    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "interval_seconds")
    private Long intervalSeconds;

    @Column(name = "last_notification_at")
    private LocalDateTime lastNotificationAt;

    @Column(name = "next_notification_at")
    private LocalDateTime nextNotificationAt;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "notify_when_vacancies_not_found", nullable = false)
    private boolean notifyWhenVacanciesNotFound;

    public NotificationSettingsEntity(long chatId) {
        this.chatId = chatId;
    }
}