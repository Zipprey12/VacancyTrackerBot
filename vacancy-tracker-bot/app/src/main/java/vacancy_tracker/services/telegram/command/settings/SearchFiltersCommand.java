package vacancy_tracker.services.telegram.command.settings;

import org.springframework.context.ApplicationEventPublisher;
import vacancy_tracker.model.telegram.MessageData;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.events.SettingCommandExecutionEvent;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;

public abstract class SearchFiltersCommand extends InputInterceptingCommand {

    protected SearchFiltersCommand(String key,
                                   String description,
                                   MessageSender sender,
                                   SessionsService sessionsService,
                                   InputInterceptor inputInterceptor,
                                   ApplicationEventPublisher eventPublisher) {
        super(key, description, sender, eventPublisher, sessionsService, inputInterceptor);
    }

    @Override
    public void handleInputEnd(MessageData messageData) {
        getEventPublisher().publishEvent(new SettingCommandExecutionEvent(this, messageData));
    }
}
