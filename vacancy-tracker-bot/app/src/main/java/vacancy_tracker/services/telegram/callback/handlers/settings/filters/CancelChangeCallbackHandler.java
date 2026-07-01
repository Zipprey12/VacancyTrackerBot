package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.events.FilterSettingsEvent;
import vacancy_tracker.services.telegram.session.SessionsService;

@Component
public class CancelChangeCallbackHandler extends CallbackHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final SessionsService service;

    public CancelChangeCallbackHandler(ApplicationEventPublisher eventPublisher, SessionsService service) {
        super(FilterSettingsCallbackKeys.CANCEL_CHANGE.getKey());
        this.eventPublisher = eventPublisher;
        this.service = service;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var messageData = MessageData.create(callbackQuery);
        service.disableInterceptor(messageData.getChatId());
        eventPublisher.publishEvent(new FilterSettingsEvent(this, messageData, true, null));
    }
}