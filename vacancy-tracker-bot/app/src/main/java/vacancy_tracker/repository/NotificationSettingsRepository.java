package vacancy_tracker.repository;

import vacancy_tracker.model.telegram.NotificationSettings;

import java.util.Optional;

public interface NotificationSettingsRepository {

    Optional<NotificationSettings> get(long sessionId);

    void save(long sessionId, NotificationSettings settings);

    void remove(long sessionId);

    NotificationSettings getOrCreate(long sessionId);
}
