package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.NotificationChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.TimeInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.TimeSelectionMessageFormatter;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static vacancy_tracker.services.DateUtil.nextTime;

@Component
public class SetNotificationTimeCommand extends InputInterceptingCommand<LocalTime> {

    private static final String KEY = "set_notification_time";

    private final NotificationService notificationService;
    private final TimeSelectionMessageFormatter messageFormatter;

    protected SetNotificationTimeCommand(SendingAndUpdatingMessagePublisher publisher,
                                         SessionsService sessionsService,
                                         NotificationService notificationService,
                                         NotificationChangingCompletionHandler handler,
                                         TimeSelectionMessageFormatter messageFormatter) {
        super(KEY, null, publisher, handler, new TimeInterceptor(), sessionsService);
        this.notificationService = notificationService;
        this.messageFormatter = messageFormatter;
    }

    @Override
    protected void executeWithParameters(MessageData messageData, LocalTime parameter) {
        var sessionId = messageData.getChatId();
        var settings = notificationService.get(sessionId);
        var next = nextTime(LocalDateTime.now(), parameter);

        settings.setNextNotificationAt(next);
        notificationService.save(sessionId, settings);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = notificationService.get(messageData.getChatId());
        messageFormatter.format(messageData, settings);
    }
}
