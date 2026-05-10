package vacancy_tracker.services.telegram.command.settings;

import org.springframework.context.ApplicationEventPublisher;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.events.SettingCommandExecutionEvent;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;

public abstract class SearchFiltersCommand extends InputInterceptingCommand {

    protected SearchFiltersCommand(String key,
                                   String description,
                                   MessageSender sender,
                                   MessageEditor editor,
                                   SessionsService sessionsService,
                                   InputInterceptor inputInterceptor,
                                   ApplicationEventPublisher eventPublisher) {
        super(key, description, sender, editor, eventPublisher, sessionsService, inputInterceptor);
    }

    @Override
    public void handleExecutionEnd(MessageData messageData, boolean isInterceptorUsed) {
        disableInterceptor(messageData.getChatId());
        getEventPublisher().publishEvent(new SettingCommandExecutionEvent(this, messageData, isInterceptorUsed));
    }
}
