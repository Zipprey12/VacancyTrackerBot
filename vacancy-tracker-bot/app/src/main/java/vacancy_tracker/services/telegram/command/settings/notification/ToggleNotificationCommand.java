package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.ExtendedMessageCommand;
import vacancy_tracker.services.telegram.command.handlers.NotificationChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.notification.NotificationQueueService;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.NotificationSettingsMessageFormatter;

@Component
public class ToggleNotificationCommand extends ExtendedMessageCommand<Boolean> {

    public static final String KEY = "/set_notification";
    public static final String DESCRIPTION = "Включить / отключить уведомления";

    private final NotificationService service;
    private final NotificationQueueService queueService;
    private final NotificationSettingsMessageFormatter messageFormatter;

    protected ToggleNotificationCommand(SendingAndUpdatingMessagePublisher publisher,
                                        NotificationChangingCompletionHandler handler,
                                        NotificationSettingsMessageFormatter messageFormatter,
                                        NotificationService notificationService,
                                        NotificationQueueService queueService) {
        super(KEY, DESCRIPTION, publisher);

        this.service = notificationService;
        this.messageFormatter = messageFormatter;
        this.queueService = queueService;
        setOnComplete(handler);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = service.get(messageData.getChatId());
        messageData.setText(messageFormatter.generateText(settings));
        messageData.setKeyboardMarkup(messageFormatter.generateOnOffButton(settings.isEnabled()));
    }

    @Override
    protected void executeWithParameters(MessageData messageData, Boolean parameter) {
        var chatId = messageData.getChatId();
        var settings = service.get(chatId);

        var value = parameter != null && parameter;
        settings.setEnabled(value);

        if (value) {
            queueService.schedule(chatId, settings);
        } else {
            queueService.cancel(chatId);
        }
        service.save(chatId, settings);
    }
}