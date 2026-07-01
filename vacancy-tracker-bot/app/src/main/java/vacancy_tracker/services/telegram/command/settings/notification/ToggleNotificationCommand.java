package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.ExtendedMessageCommand;
import vacancy_tracker.services.telegram.command.handlers.NotificationChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.NotificationSettingsMessageFormatter;

@Component
public class ToggleNotificationCommand extends ExtendedMessageCommand<Boolean> {

    public static final String KEY = "/toggle_notification";
    public static final String DESCRIPTION = "Включить / отключить уведомления";

    private final NotificationService service;
    private final NotificationSettingsMessageFormatter messageFormatter;

    protected ToggleNotificationCommand(SendingAndUpdatingMessagePublisher publisher,
                                        NotificationChangingCompletionHandler handler,
                                        NotificationSettingsMessageFormatter messageFormatter,
                                        NotificationService notificationService,
                                        SequentialAsyncExecutionStrategy strategy) {
        super(new CommandArgs(KEY, DESCRIPTION, null, CommandCategory.NOTIFICATION), publisher, strategy);

        this.service = notificationService;
        this.messageFormatter = messageFormatter;
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
        service.save(chatId, settings);
    }
}