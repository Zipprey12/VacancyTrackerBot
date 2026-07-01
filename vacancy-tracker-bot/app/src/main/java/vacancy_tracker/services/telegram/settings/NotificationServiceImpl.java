package vacancy_tracker.services.telegram.settings;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.notification.NotificationSettings;
import vacancy_tracker.repository.NotificationSettingsRepository;
import vacancy_tracker.services.telegram.notification.NotificationQueueService;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationSettingsRepository repository;
    private final NotificationQueueService queueService;

    public NotificationServiceImpl(NotificationSettingsRepository repository,
                                   @Lazy NotificationQueueService queueService) {
        this.repository = repository;
        this.queueService = queueService;
    }

    @Override
    @Cacheable(value = "notifications", key = "#sessionId")
    public NotificationSettings get(long sessionId) {
        return repository.getOrCreate(sessionId);
    }

    @Override
    @CachePut(value = "notifications", key = "#sessionId")
    public NotificationSettings save(long sessionId, NotificationSettings settings) {
        var last = settings.getLastNotificationAt();
        var next = settings.getNextNotificationAt();
        if (last != null && next != null && last.isAfter(next)) {
            settings.setLastNotificationAt(next);
        }
        queueService.schedule(sessionId, settings);
        return repository.save(sessionId, settings);
    }

    @Override
    @CacheEvict(value = "notifications", key = "#sessionId")
    public void remove(long sessionId) {
        repository.remove(sessionId);
        queueService.cancel(sessionId);
    }

    @Override
    public List<NotificationSettings> findEnabled() {
        return repository.findEnabled();
    }
}