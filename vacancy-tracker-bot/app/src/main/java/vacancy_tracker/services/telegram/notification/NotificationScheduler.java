package vacancy_tracker.services.telegram.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.RequestType;
import vacancy_tracker.model.telegram.CallingSource;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.VacanciesSearchParams;
import vacancy_tracker.services.telegram.command.vacancies.SendAllVacanciesCommand;
import vacancy_tracker.services.telegram.settings.NotificationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final int MAX_BATCH_SIZE = 10;

    private final NotificationQueueService queueService;
    private final NotificationService notificationService;
    private final SendAllVacanciesCommand sendCommand;

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
        var settings = notificationService.get(chatId);
        if (settings == null || !settings.isEnabled()) {
            return;
        }

        var data = MessageData.builder()
                .chatId(chatId)
                .source(CallingSource.CHAT)
                .build();

        var params = VacanciesSearchParams.builder()
                .page(0)
                .startDate(settings.getLastNotificationAt())
                .requestType(RequestType.SCHEDULED)
                .build();

        sendCommand.executeWithCompletionCheck(data, params)
                .thenAccept(success -> {
                    if (Boolean.TRUE.equals(success)) {
                        scheduleNext(settings, chatId);
                    } else {
                        log.error("Ошибка при отправке уведомления chatId={}", chatId);
                    }
                });
    }

    private void scheduleNext(NotificationSettings settings, long chatId) {
        settings.scheduleNext();
        queueService.schedule(chatId, settings);
        notificationService.save(chatId, settings);
    }
}
