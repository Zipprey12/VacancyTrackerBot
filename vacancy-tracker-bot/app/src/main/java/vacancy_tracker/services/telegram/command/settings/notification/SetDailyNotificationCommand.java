package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.TimeSelectionMessageFormatter;

import java.time.Duration;

@Component
public class SetDailyNotificationCommand extends CompletableMessageCommand {

    private final NotificationService notificationService;
    private final TimeSelectionMessageFormatter messageFormatter;

    protected SetDailyNotificationCommand(SendingAndUpdatingMessagePublisher publisher,
                                          NotificationService notificationService,
                                          TimeSelectionMessageFormatter messageFormatter) {
        super(publisher);
        this.notificationService = notificationService;
        this.messageFormatter = messageFormatter;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var chatId = messageData.getChatId();
        var settings = notificationService.get(chatId);

        settings.setInterval(Duration.ofDays(1));
        settings.scheduleNext();

        notificationService.save(chatId, settings);
        messageFormatter.format(messageData, settings);
    }
}