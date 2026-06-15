package vacancy_tracker.services.telegram.actions.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.actions.MessageAction;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.settings.notification.SetNotificationTimeCommand;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.settings.NotificationService;

import java.time.Duration;

@Component
public class SetDailyNotificationAction extends MessageAction {

    private final NotificationService notificationService;
    private final SetNotificationTimeCommand command;

    protected SetDailyNotificationAction(SendingAndUpdatingMessagePublisher publisher,
                                         ExecutionStrategy asyncExecutionStrategy,
                                         NotificationService notificationService,
                                         SetNotificationTimeCommand command) {
        super(asyncExecutionStrategy, publisher);
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