package vacancy_tracker.services.telegram.callback.handlers;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.MessageDataHandlerCommand;

public class SimpleMessageCallbackHandler extends CallbackHandler {

    private final MessageDataHandlerCommand handler;

    public SimpleMessageCallbackHandler(String callbackKey, MessageDataHandlerCommand handler) {
        super(callbackKey);
        this.handler = handler;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        handler.handleData(MessageData.create(message), true);
    }
}
