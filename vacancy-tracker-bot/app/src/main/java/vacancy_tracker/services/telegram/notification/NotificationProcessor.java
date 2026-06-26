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
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.settings.NotificationService;

import java.time.LocalDateTime;
import java.util.Objects;

import static vacancy_tracker.model.telegram.execution.ExecutionFailReason.EXCEPTION;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProcessor {

    private final NotificationService notificationService;
    private final SendVacanciesAction showVacanciesAction;
    private final SequentialAsyncExecutionStrategy executionStrategy;

    @Async
    public void processAsync(long chatId) {
        var settings = notificationService.get(chatId);
        if (settings == null || !settings.isEnabled()) return;

        var nextNotification = settings.getNextNotificationAt();
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
                .thenAccept(result -> handleCommandExecution(result, chatId, nextNotification));
    }

    private void handleCommandExecution(ExecutionResult result, long chatId, LocalDateTime nextNotification) {
        if (!result.isSuccess() && EXCEPTION.equals(result.getFailReason())) {
            log.error("Ошибка при отправке уведомления chatId={}", chatId);
            return;
        }

        executionStrategy.execute(chatId, () -> {
            var settings = notificationService.get(chatId);
            var currentNextNotification = settings.getNextNotificationAt();

            if (!Objects.equals(nextNotification, currentNextNotification)) {
                log.debug("Настройки уведомлений изменились во время выполнения, " +
                        "планирование следующего уведомления пропущено chatId={}", chatId);
                return;
            }
            log.info("Success: {}", result.isSuccess());

            if (result.isSuccess()) {
                scheduleNext(settings, chatId);
                log.info("schedule next");
            } else {
                rescheduleNext(settings, chatId);
                log.info("rescheduleNext next");
            }
        });
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
