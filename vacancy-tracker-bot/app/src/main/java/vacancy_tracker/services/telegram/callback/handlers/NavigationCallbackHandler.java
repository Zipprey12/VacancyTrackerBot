package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;
import vacancy_tracker.services.telegram.handlers.ParametrizedDataHandler;
import vacancy_tracker.services.telegram.view.keyboard.CallbackPaginatedKeyboardBuilder;

import java.util.List;

public abstract class NavigationCallbackHandler<T> extends ParsingDataCallbackHandler<T> {

    @Getter(AccessLevel.PROTECTED)
    private final CallbackPaginatedKeyboardBuilder keyboardBuilder;
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private List<CallbackItem> defaultItems;

    protected NavigationCallbackHandler(String callbackKey,
                                        ParametrizedDataHandler<T> handler) {
        super(callbackKey, handler);
        this.keyboardBuilder = new CallbackPaginatedKeyboardBuilder((PaginationCallbackParser) getCallbackParser());
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var text = callbackQuery.getData();
        var data = getCallbackParser().parse(text);

        var message = callbackQuery.getMessage();
        var messageData = MessageData.create(message);

        if (data.isIgnored()) {
            return;
        }
        if (data.isPageNavigation()) {
            navigate(messageData, data);
            return;
        }
        if (data.isSelection()) {
            select(data, messageData);
            return;
        }
        if (data.hasEmptyKey()) {
            executeWithEmptyKey(messageData, data);
        }
    }

    @Override
    protected PaginationCallbackParser initCallbackParser() {
        return new PaginationCallbackParser(getKey());
    }

    protected abstract void navigate(MessageData message, CallbackData data);
}