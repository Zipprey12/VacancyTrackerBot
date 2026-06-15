package vacancy_tracker.repository.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vacancy_tracker.model.persistence.NotificationSettingsEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationSettingsDao extends JpaRepository<NotificationSettingsEntity, Long> {

    List<NotificationSettingsEntity> findByNextNotificationAtLessThanEqual(LocalDateTime dateTime);

    List<NotificationSettingsEntity> findByEnabledTrueAndNextNotificationAtLessThanEqual(LocalDateTime dateTime);

    List<NotificationSettingsEntity> findByEnabledTrue();
}
