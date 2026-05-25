package vacancy_tracker.services.telegram.settings;

import vacancy_tracker.model.telegram.NotificationSettings;

import java.util.List;

public interface NotificationService {

    NotificationSettings get(long sessionId);

    void save(long sessionId, NotificationSettings settings);

    void remove(long sessionId);

    List<NotificationSettings> findEnabled();
}
