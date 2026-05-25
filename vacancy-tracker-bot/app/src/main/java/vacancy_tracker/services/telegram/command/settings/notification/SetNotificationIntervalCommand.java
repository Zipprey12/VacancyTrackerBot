package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.NotificationIntervalMessageFormatter;

@Component
public class SetNotificationIntervalCommand extends CompletableMessageCommand {

    public static final String KEY = "/set_interval";
    public static final String DESCRIPTION = "Задать интервал отправки уведомлений";

    private final NotificationService notificationService;
    private final NotificationIntervalMessageFormatter messageFormatter;

    protected SetNotificationIntervalCommand(SendingAndUpdatingMessagePublisher publisher,
                                             NotificationService notificationService,
                                             NotificationIntervalMessageFormatter messageFormatter) {
        super(KEY, DESCRIPTION, publisher);
        this.notificationService = notificationService;
        this.messageFormatter = messageFormatter;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = notificationService.get(messageData.getChatId());
        messageFormatter.fill(messageData, settings);
    }
}
