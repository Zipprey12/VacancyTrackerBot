package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.handlers.NotificationChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.ToggleEmptyNotifyMessageFormatter;


@Component
public class ToggleEmptyNotifyCommand extends CompletableMessageCommand {

    public static final String KEY = "/set_empty_notify";
    public static final String DESCRIPTION = "Включить / отключить уведомления при отсутствии новых вакансий";

    private final NotificationService service;
    private final ToggleEmptyNotifyMessageFormatter messageFormatter;

    protected ToggleEmptyNotifyCommand(SendingAndUpdatingMessagePublisher publisher,
                                       NotificationChangingCompletionHandler handler,
                                       NotificationService service,
                                       ToggleEmptyNotifyMessageFormatter messageFormatter) {
        super(KEY, DESCRIPTION, publisher);
        this.service = service;
        this.messageFormatter = messageFormatter;

        setOnComplete(handler);

    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = service.get(messageData.getChatId());
        boolean isEnabled = settings.isNotifyWhenVacanciesNotFound();
        messageFormatter.format(isEnabled, messageData);
    }
}
