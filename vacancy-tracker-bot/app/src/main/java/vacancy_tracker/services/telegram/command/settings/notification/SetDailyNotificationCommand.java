package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.SimpleMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.NotificationService;

import java.time.Duration;

@Component
public class SetDailyNotificationCommand extends SimpleMessageCommand {

    private final NotificationService notificationService;
    private final SetNotificationTimeCommand command;

    protected SetDailyNotificationCommand(SendingAndUpdatingMessagePublisher publisher,
                                          NotificationService notificationService,
                                          SetNotificationTimeCommand command) {
        super(publisher);
        this.notificationService = notificationService;
        this.command = command;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var chatId = messageData.getChatId();
        var settings = notificationService.get(chatId);

        settings.setInterval(Duration.ofDays(1));
        notificationService.save(chatId, settings);
        command.execute(messageData);
    }
}