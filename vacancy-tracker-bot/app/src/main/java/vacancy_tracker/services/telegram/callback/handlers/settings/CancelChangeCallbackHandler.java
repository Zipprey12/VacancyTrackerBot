package vacancy_tracker.services.telegram.callback.handlers.settings;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.events.FilterSettingsEvent;

@Component
public class CancelChangeCallbackHandler extends CallbackHandler {

    private final ApplicationEventPublisher eventPublisher;

    public CancelChangeCallbackHandler(ApplicationEventPublisher eventPublisher) {
        super(FilterSettingsCallbackKeys.CANCEL_FILTER_CHANGE.getKey());
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        var messageData = MessageData.create(message);
        eventPublisher.publishEvent(new FilterSettingsEvent(this, messageData,
                false, true));
    }
}