package vacancy_tracker.services.telegram.settings;

import vacancy_tracker.model.telegram.NotificationSettings;

public interface NotificationService {

    NotificationSettings get(long sessionId);

    void save(long sessionId, NotificationSettings settings);

    void remove(long sessionId);
}
