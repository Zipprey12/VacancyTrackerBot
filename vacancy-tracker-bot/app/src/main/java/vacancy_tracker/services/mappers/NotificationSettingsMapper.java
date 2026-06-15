package vacancy_tracker.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vacancy_tracker.model.persistence.NotificationSettingsEntity;
import vacancy_tracker.model.telegram.notification.NotificationSettings;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface NotificationSettingsMapper {

    @Mapping(target = "lastNotificationAt", source = "lastNotificationAt")
    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "interval", expression = "java(toInterval(entity))")
    NotificationSettings toDto(NotificationSettingsEntity entity);

    @Mapping(target = "intervalSeconds", expression = "java(toSeconds(settings))")
    @Mapping(target = "lastNotificationAt", source = "lastNotificationAt")
    @Mapping(target = "nextNotificationAt", source = "nextNotificationAt")
    void updateEntity(NotificationSettings settings, @MappingTarget NotificationSettingsEntity entity);

    default Duration toInterval(NotificationSettingsEntity entity) {
        if (entity.getIntervalSeconds() == null) return null;
        return Duration.ofSeconds(entity.getIntervalSeconds());
    }

    default Long toSeconds(NotificationSettings settings) {
        if (settings.getInterval() == null) return null;
        return settings.getInterval().toSeconds();
    }
}