package vacancy_tracker.repository.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.model.telegram.entities.NotificationSettingsEntity;
import vacancy_tracker.repository.NotificationSettingsRepository;
import vacancy_tracker.repository.jpa.dao.NotificationSettingsDao;
import vacancy_tracker.services.mappers.NotificationSettingsMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaNotificationSettingsRepository implements NotificationSettingsRepository {

    private final NotificationSettingsDao notificationSettingsDao;
    private final NotificationSettingsMapper mapper;

    @Override
    public Optional<NotificationSettings> get(long chatId) {
        return notificationSettingsDao.findById(chatId)
                .map(mapper::toDto);
    }

    @Override
    public void save(long chatId, NotificationSettings settings) {
        var entity = new NotificationSettingsEntity(chatId);
        mapper.updateEntity(settings, entity);
        notificationSettingsDao.save(entity);
    }

    @Override
    public void remove(long chatId) {
        notificationSettingsDao.deleteById(chatId);
    }

    @Override
    public NotificationSettings getOrCreate(long chatId) {
        return notificationSettingsDao.findById(chatId)
                .map(mapper::toDto)
                .orElseGet(() -> {
                    var settings = new NotificationSettings();
                    save(chatId, settings);
                    return settings;
                });
    }

    @Override
    public List<NotificationSettings> findAllDue(LocalDateTime dateTime, boolean onlyEnabled) {
        List<NotificationSettingsEntity> entities;

        if (onlyEnabled) {
            entities = notificationSettingsDao
                    .findByEnabledTrueAndNextNotificationAtLessThanEqual(dateTime);
        } else {
            entities = notificationSettingsDao
                    .findByNextNotificationAtLessThanEqual(dateTime);
        }

        return entities.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<NotificationSettings> findEnabled() {
        var entities = notificationSettingsDao.findByEnabledTrue();
        return entities.stream()
                .map(mapper::toDto)
                .toList();
    }
}