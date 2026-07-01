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
import vacancy_tracker.services.telegram.view.formatters.notification.ToggleEmptyNotifyMessageFormatter;


@Component
public class ToggleEmptyNotifyCommand extends ExtendedMessageCommand<Boolean> {

    public static final String KEY = "/empty_notify";
    public static final String DESCRIPTION = "Уведомления при отсутствии вакансий";

    private final NotificationService service;
    private final ToggleEmptyNotifyMessageFormatter messageFormatter;

    protected ToggleEmptyNotifyCommand(SendingAndUpdatingMessagePublisher publisher,
                                       NotificationChangingCompletionHandler handler,
                                       NotificationService service,
                                       ToggleEmptyNotifyMessageFormatter messageFormatter,
                                       SequentialAsyncExecutionStrategy strategy) {
        super(new CommandArgs(KEY, DESCRIPTION, null, CommandCategory.NOTIFICATION), publisher, strategy);
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

    @Override
    protected void executeWithParameters(MessageData messageData, Boolean parameter) {
        var chatId = messageData.getChatId();
        var settings = service.get(chatId);
        settings.setNotifyWhenVacanciesNotFound(parameter);
        service.save(chatId, settings);
    }
}
