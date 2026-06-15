package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.notification.NotificationSettings;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.NotificationChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.TimeInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.TimeSelectionMessageFormatter;
import vacancy_tracker.services.util.DateUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class SetNotificationTimeCommand extends InputInterceptingCommand<LocalTime> {

    private static final String KEY = "set_notification_time";

    private final NotificationService notificationService;
    private final TimeSelectionMessageFormatter messageFormatter;

    protected SetNotificationTimeCommand(SendingAndUpdatingMessagePublisher publisher,
                                         SessionsService sessionsService,
                                         NotificationService notificationService,
                                         NotificationChangingCompletionHandler handler,
                                         TimeSelectionMessageFormatter messageFormatter,
                                         SequentialAsyncExecutionStrategy strategy) {
        super(KEY, null, publisher, handler,
                new TimeInterceptor(), sessionsService, strategy);
        this.notificationService = notificationService;
        this.messageFormatter = messageFormatter;
    }

    @Override
    protected void executeWithParameters(MessageData messageData, LocalTime parameter) {
        var sessionId = messageData.getChatId();
        var settings = notificationService.get(sessionId);
        var next = calculateNext(settings, parameter);
        settings.setNextNotificationAt(next);
        notificationService.save(sessionId, settings);
    }

    private LocalDateTime calculateNext(NotificationSettings settings, LocalTime newTime) {
        var now = LocalDateTime.now();
        var interval = settings.getInterval();

        if (Duration.ofDays(7).equals(interval)) {
            if (settings.getNextNotificationAt() != null) {
                var dayOfWeek = settings.getNextNotificationAt().getDayOfWeek().getValue();
                return DateUtil.nextDayOfWeek(now, dayOfWeek, newTime);
            }
            return now.plusWeeks(1).with(newTime);
        }

        if (settings.getNextNotificationAt() != null) {
            return DateUtil.nextTimeWithInterval(settings.getNextNotificationAt(), newTime, interval);
        }

        return now.plus(interval).with(newTime);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = notificationService.get(messageData.getChatId());
        messageFormatter.format(messageData, settings);
    }
}
