package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.handlers.ExecutableMessageHandler;

public class SimpleMessageCallbackHandler extends CallbackHandler {

    @Getter(AccessLevel.PROTECTED)
    private final ExecutableMessageHandler handler;

    public SimpleMessageCallbackHandler(String callbackKey, ExecutableMessageHandler handler) {
        super(callbackKey);
        this.handler = handler;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        handler.execute(MessageData.create(message));
    }
}
