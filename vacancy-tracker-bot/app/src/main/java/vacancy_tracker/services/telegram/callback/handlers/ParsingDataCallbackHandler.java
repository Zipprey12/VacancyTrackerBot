package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.parsers.CallbackParser;
import vacancy_tracker.services.telegram.command.MessageDataHandlerCommand;

import java.util.Optional;

@Slf4j
public abstract class ParsingDataCallbackHandler<T> extends SimpleMessageCallbackHandler {

    @Getter(AccessLevel.PROTECTED)
    private final CallbackParser callbackParser;

    protected ParsingDataCallbackHandler(String callbackKey, MessageDataHandlerCommand handler) {
        super(callbackKey, handler);
        this.callbackParser = initCallbackParser();
    }

    protected abstract Optional<T> tryCastSelectedValue(String value);
    public abstract void handleCastedData(T data, MessageData messageData);

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var text = callbackQuery.getData();
        var callbackData = callbackParser.parse(text);

        var messageData = MessageData.create(callbackQuery.getMessage());
        if (callbackData.isEmpty()) {
            executeWithNoArgs(messageData);
        } else {
            select(callbackData, messageData);
        }
    }

    protected void executeWithNoArgs(MessageData messageData) {
        getHandler().execute(messageData, true);
    }

    protected CallbackParser initCallbackParser() {
        return new CallbackParser(getKey());
    }

    protected final void select(CallbackData data, MessageData messageData) {
        var value = data.selectedKey();
        var casted = tryCastSelectedValue(value);

        if(casted.isEmpty()){
            log.error("Передано недопустимое значение для Callback {} : {}", getKey(), value);
        }
        else {
            handleCastedData(casted.get(), messageData);
        }
    }
}
