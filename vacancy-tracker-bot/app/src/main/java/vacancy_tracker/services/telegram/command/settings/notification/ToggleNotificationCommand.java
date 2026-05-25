package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.handlers.NotificationChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.UpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.NotificationSettingsMessageFormatter;

@Component
public class ToggleNotificationCommand extends CompletableMessageCommand {

    public static final String KEY = "/set_notification";
    public static final String DESCRIPTION = "Включить / отключить уведомления";

    private final NotificationService service;
    private final NotificationSettingsMessageFormatter messageFormatter;

    protected ToggleNotificationCommand(UpdatingMessagePublisher publisher,
                                        NotificationChangingCompletionHandler handler,
                                        NotificationSettingsMessageFormatter messageFormatter,
                                        NotificationService notificationService) {
        super(KEY, DESCRIPTION, publisher);

        this.service = notificationService;
        this.messageFormatter = messageFormatter;
        setOnComplete(handler);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = service.get(messageData.getMessageId());
        messageData.setText(messageFormatter.generateText(settings));
        messageData.setKeyboardMarkup(messageFormatter.generateOnOffButton(settings.isEnabled()));
    }
}