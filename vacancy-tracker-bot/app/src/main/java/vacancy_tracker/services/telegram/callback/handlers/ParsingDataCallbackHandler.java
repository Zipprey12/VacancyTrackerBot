package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.parsers.CallbackParser;
import vacancy_tracker.services.telegram.handlers.ParametrizedDataHandler;

import java.util.Optional;

@Slf4j
public abstract class ParsingDataCallbackHandler<T> extends CallbackHandler {

    @Getter
    private final ParametrizedDataHandler<T> handler;

    @Getter(AccessLevel.PROTECTED)
    private final CallbackParser callbackParser;

    protected ParsingDataCallbackHandler(String callbackKey, ParametrizedDataHandler<T> handler) {
        super(callbackKey);
        this.handler = handler;
        this.callbackParser = initCallbackParser();
    }

    protected abstract Optional<T> tryCastSelectedValue(String value);

    public void handleCastedData(T data, MessageData messageData) {
        handler.handleWithParameter(messageData, data);
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var text = callbackQuery.getData();
        var callbackData = callbackParser.parse(text);

        var messageData = MessageData.create(callbackQuery);
        if (callbackData.hasEmptyKey()) {
            executeWithEmptyKey(messageData, callbackData);
        } else {
            select(callbackData, messageData);
        }
    }

    protected void executeWithEmptyKey(MessageData messageData, CallbackData data) {
        if (data.args() != null) {
            log.error("Аргументы Callback {} не были обработаны", data.prefix());
        }
        handler.execute(messageData);
    }

    protected CallbackParser initCallbackParser() {
        return new CallbackParser(getKey());
    }

    protected final void select(CallbackData data, MessageData messageData) {
        var value = data.selectedKey();
        var casted = tryCastSelectedValue(value);

        if (casted.isEmpty()) {
            log.error("Передано недопустимое значение для Callback {} : {}", getKey(), value);
        } else {
            handleCastedData(casted.get(), messageData);
        }
    }
}
