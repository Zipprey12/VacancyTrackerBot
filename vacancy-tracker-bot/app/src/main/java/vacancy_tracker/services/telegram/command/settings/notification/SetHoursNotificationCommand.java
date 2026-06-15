package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.NotificationChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.DurationInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.NotificationHoursSelectionFormatter;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class SetHoursNotificationCommand extends InputInterceptingCommand<Duration> {

    public static final String KEY = "/set_hours_interval";
    public static final String DESCRIPTION = "Задать интервал уведомлений в часах";
    public static final Duration MIN_INTERVAL = Duration.ofMinutes(5);

    private final NotificationHoursSelectionFormatter formatter;
    private final NotificationService notificationService;

    protected SetHoursNotificationCommand(SendingAndUpdatingMessagePublisher publisher,
                                          SequentialAsyncExecutionStrategy executionStrategy,
                                          NotificationChangingCompletionHandler handler,
                                          SessionsService sessionsService,
                                          NotificationService notificationService,
                                          NotificationHoursSelectionFormatter formatter) {
        super(KEY, DESCRIPTION, publisher, handler, new DurationInterceptor(), sessionsService, executionStrategy);
        this.formatter = formatter;
        this.notificationService = notificationService;
    }

    @Override
    protected void executeWithParameters(MessageData messageData, Duration parameter) {
        if (parameter.isNegative() || parameter.compareTo(MIN_INTERVAL) < 0) {
            handleInvalidValue(messageData, "Интервал не может быть меньше 5 минут");
            return;
        }

        var settings = notificationService.get(messageData.getChatId());
        settings.setInterval(parameter);

        var now = LocalDateTime.now();
        var previous = settings.getLastNotificationAt();
        if (previous == null) {
            settings.setNextNotificationAt(now);
        } else {
            var next = previous.plus(parameter);
            if (next.isBefore(now)) {
                settings.setNextNotificationAt(now);
            } else {
                settings.setNextNotificationAt(next);
            }
        }
        notificationService.save(messageData.getChatId(), settings);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = notificationService.get(messageData.getChatId());
        formatter.format(messageData, settings);
    }
}
