package vacancy_tracker.repository;

import vacancy_tracker.model.telegram.notification.NotificationSettings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationSettingsRepository {

    Optional<NotificationSettings> get(long sessionId);

    NotificationSettings save(long sessionId, NotificationSettings settings);

    void remove(long sessionId);

    NotificationSettings getOrCreate(long sessionId);

    List<NotificationSettings> findAllDue(LocalDateTime dateTime, boolean onlyEnabled);

    List<NotificationSettings> findEnabled();
}
