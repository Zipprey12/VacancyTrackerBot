package vacancy_tracker.services.telegram.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.domain.RequestType;
import vacancy_tracker.model.search.VacanciesSearchRequest;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.SearchActionParams;
import vacancy_tracker.model.telegram.execution.ExecutionResult;
import vacancy_tracker.model.telegram.notification.NotificationSettings;
import vacancy_tracker.model.telegram.session.PublishType;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;
import vacancy_tracker.services.telegram.actions.vacancies.SendVacanciesAction;
import vacancy_tracker.services.telegram.settings.NotificationService;

import java.time.Instant;

import static vacancy_tracker.model.telegram.execution.ExecutionFailReason.EXCEPTION;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProcessor {

    private final NotificationService notificationService;
    private final SendVacanciesAction showVacanciesAction;

    @Async
    public void processAsync(long chatId) {
        var start = Instant.now();
        var settings = notificationService.get(chatId);
        if (settings == null || !settings.isEnabled()) return;

        var data = MessageData.builder()
                .chatId(chatId)
                .source(PublishType.SEND)
                .build();
        var params = VacanciesSearchRequest.builder()
                .page(0)
                .startDate(settings.getLastNotificationAt())
                .requestType(RequestType.SCHEDULED)
                .build();

        showVacanciesAction.executeWithCompletionCheck(data, new SearchActionParams(params,
                        new VacanciesShownParams(settings.isNotifyWhenVacanciesNotFound(), true)))
                .thenAccept(result -> handleCommandExecution(result, chatId, start));
    }

    private void handleCommandExecution(ExecutionResult result, long chatId, Instant start) {
        if (!result.isSuccess() && result.getFailReason().equals(EXCEPTION)) {
            log.error("Ошибка при отправке уведомления chatId={}", chatId);
            return;
        }

        var settings = notificationService.get(chatId);
        var lastUpdate = settings.getUpdatedAt();
        if (lastUpdate == null || start.isAfter(lastUpdate)) {
            if (result.isSuccess()) {
                scheduleNext(settings, chatId);
            } else {
                rescheduleNext(settings, chatId);
            }
        }
    }

    private void scheduleNext(NotificationSettings settings, long chatId) {
        settings.scheduleNext();
        save(settings, chatId);
    }

    private void rescheduleNext(NotificationSettings settings, long chatId) {
        settings.rescheduleNext();
        save(settings, chatId);
    }

    private void save(NotificationSettings settings, long chatId) {
        notificationService.save(chatId, settings);
    }
}
