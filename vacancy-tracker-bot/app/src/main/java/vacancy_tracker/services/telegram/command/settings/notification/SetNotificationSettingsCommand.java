package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.SimpleMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.NotificationSettingsMessageFormatter;

@Component
public class SetNotificationSettingsCommand extends SimpleMessageCommand {

    public static final String KEY = "/notification";
    public static final String DESCRIPTION = "Настройка уведомлений";

    private final NotificationService settingsService;
    private final NotificationSettingsMessageFormatter messageFormatter;

    protected SetNotificationSettingsCommand(SendingAndUpdatingMessagePublisher publisher,
                                             NotificationService settingsService,
                                             NotificationSettingsMessageFormatter messageFormatter) {
        super(KEY, DESCRIPTION, publisher);
        this.settingsService = settingsService;
        this.messageFormatter = messageFormatter;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = settingsService.get(messageData.getChatId());
        messageFormatter.fill(messageData, settings);
    }
}