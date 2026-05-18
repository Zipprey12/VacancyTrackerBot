package vacancy_tracker.services.telegram.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.repository.NotificationSettingsRepository;

@Service
@RequiredArgsConstructor
public class SimpleNotificationService implements NotificationService {

    private final NotificationSettingsRepository repository;

    @Override
    public NotificationSettings get(long sessionId) {
        return repository.getOrCreate(sessionId);
    }

    @Override
    public void save(long sessionId, NotificationSettings settings) {
        repository.save(sessionId, settings);
    }

    @Override
    public void remove(long sessionId) {
        repository.remove(sessionId);
    }
}