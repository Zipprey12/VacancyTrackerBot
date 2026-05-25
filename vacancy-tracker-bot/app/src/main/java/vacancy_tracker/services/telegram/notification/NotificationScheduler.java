package vacancy_tracker.services.telegram.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.services.telegram.command.simple.ForceSearchVacanciesCommand;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final int MAX_BATCH_SIZE = 10;

    private final NotificationQueueService queueService;
    private final NotificationService notificationService;
    private final SearchFiltersService searchFiltersService;
    private final ForceSearchVacanciesCommand forceSearchVacanciesCommand;

    @Scheduled(fixedDelay = 1000)
    public void processNotifications() {
        var dueChats = queueService.dequeueLaterThan(MAX_BATCH_SIZE);
        if (dueChats.isEmpty()) {
            return;
        }

        log.debug("Обработка {} уведомлений", dueChats.size());
        dueChats.forEach(this::processAsync);
    }

    @Async
    public void processAsync(long chatId) {
        try {
            var settings = notificationService.get(chatId);
            if (settings == null || !settings.isEnabled()) {
                return;
            }

            var filter = buildFilter(chatId, settings);
            forceSearchVacanciesCommand.executeWithFilter(chatId, filter);
            var next = settings.getNextNotificationAt();

            settings.scheduleNext();
            settings.setLastNotificationAt(next);
            queueService.schedule(chatId, settings);
            notificationService.save(chatId, settings);

        } catch (Exception e) {
            log.error("Ошибка при отправке уведомления chatId={}", chatId, e);
        }
    }

    private VacancySearchFilter buildFilter(long chatId, NotificationSettings settings) {
        var filter = searchFiltersService.get(chatId);

        if (settings.getLastNotificationAt() != null) {
            filter.setModifiedFrom(settings.getLastNotificationAt().toString());
        }
        return filter;
    }
}
