package vacancy_tracker.services.telegram.notification;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.services.telegram.settings.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationQueueService {

    private final NotificationQueue queue;
    private final NotificationService notificationService;

    @PostConstruct
    public void initialize() {
        log.info("Инициализация очереди нотификаций...");
        queue.clear();

        var activeSettings = notificationService.findEnabled();
        log.info("Найдено активных нотификаций: {}", activeSettings.size());

        activeSettings.forEach(s ->
                queue.add(s.getChatId(), s.getNextNotificationAt())
        );

        log.info("Очередь нотификаций инициализирована");
    }

    @Async
    public void schedule(long chatId, NotificationSettings settings) {
        if (!settings.isEnabled() || settings.getNextNotificationAt() == null) {
            queue.remove(chatId);
            return;
        }
        queue.add(chatId, settings.getNextNotificationAt());
    }

    @Async
    public void cancel(long chatId) {
        queue.remove(chatId);
    }

    public List<Long> dequeueLaterThan(int maxCount) {
        return queue.dequeueBatchLaterThan(LocalDateTime.now(), maxCount);
    }

    public void clear() {
        queue.clear();
        log.debug("Очередь нотификации очищена");
    }
}
