package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.events.NotificationSettingEvent;

@Component
public class CancelNotificationChangeCallbackHandler extends CallbackHandler {

    private static final String KEY = NotificationSettingCallbackKeys.CANCEL_CHANGE.getKey();

    private final ApplicationEventPublisher eventPublisher;

    public CancelNotificationChangeCallbackHandler(ApplicationEventPublisher eventPublisher) {
        super(KEY);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var messageData = MessageData.create(callbackQuery);
        eventPublisher.publishEvent(new NotificationSettingEvent(this, messageData));
    }
}