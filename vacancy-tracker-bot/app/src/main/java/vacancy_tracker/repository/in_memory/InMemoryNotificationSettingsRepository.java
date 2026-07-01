package vacancy_tracker.repository.in_memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import vacancy_tracker.model.telegram.notification.NotificationSettings;
import vacancy_tracker.repository.NotificationSettingsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class InMemoryNotificationSettingsRepository implements NotificationSettingsRepository {

    private final Map<Long, NotificationSettings> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<NotificationSettings> get(long sessionId) {
        return Optional.ofNullable(storage.get(sessionId));
    }

    @Override
    public NotificationSettings save(long sessionId, NotificationSettings settings) {
        storage.put(sessionId, settings);
        log.debug("Сохранены настройки уведомлений для сессии {}", sessionId);
        return settings;
    }

    @Override
    public void remove(long sessionId) {
        storage.remove(sessionId);
        log.debug("Удалены настройки уведомлений для сессии {}", sessionId);
    }

    @Override
    public NotificationSettings getOrCreate(long sessionId) {
        return storage.computeIfAbsent(sessionId, id -> {
            log.debug("Созданы настройки уведомлений по умолчанию для сессии {}", id);
            return new NotificationSettings();
        });
    }

    @Override
    public List<NotificationSettings> findAllDue(LocalDateTime dateTime, boolean onlyEnabled) {
        return storage.values().stream()
                .filter(settings -> {
                    if (onlyEnabled && !settings.isEnabled()) {
                        return false;
                    }
                    return settings.getNextNotificationAt() != null
                            && !settings.getNextNotificationAt().isAfter(dateTime);
                })
                .toList();
    }

    @Override
    public List<NotificationSettings> findEnabled() {
        return storage.values().stream()
                .filter(NotificationSettings::isEnabled)
                .toList();
    }
}