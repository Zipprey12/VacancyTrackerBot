package vacancy_tracker.services.telegram.settings;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.repository.NotificationSettingsRepository;
import vacancy_tracker.services.telegram.notification.NotificationQueueService;

import java.util.List;

@Service
public class SimpleNotificationService implements NotificationService {

    private final NotificationSettingsRepository repository;
    private final NotificationQueueService queueService;

    public SimpleNotificationService(NotificationSettingsRepository repository,
                                     @Lazy NotificationQueueService queueService) {
        this.repository = repository;
        this.queueService = queueService;
    }

    @Override
    public NotificationSettings get(long sessionId) {
        return repository.getOrCreate(sessionId);
    }

    @Override
    public void save(long sessionId, NotificationSettings settings) {
        var last = settings.getLastNotificationAt();
        if (last != null && last.isAfter(settings.getNextNotificationAt())) {
            settings.setLastNotificationAt(settings.getNextNotificationAt());
        }
        queueService.schedule(sessionId, settings);
        repository.save(sessionId, settings);
    }

    @Override
    public void remove(long sessionId) {
        repository.remove(sessionId);
        queueService.cancel(sessionId);
    }

    @Override
    public List<NotificationSettings> findEnabled() {
        return repository.findEnabled();
    }
}