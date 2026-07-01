package vacancy_tracker.services.telegram.settings;

import vacancy_tracker.model.telegram.notification.NotificationSettings;

import java.util.List;

public interface NotificationService {

    NotificationSettings get(long sessionId);

    NotificationSettings save(long sessionId, NotificationSettings settings);

    void remove(long sessionId);

    List<NotificationSettings> findEnabled();
}
