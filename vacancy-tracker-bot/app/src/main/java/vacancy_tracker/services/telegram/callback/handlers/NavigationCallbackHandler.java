package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;
import vacancy_tracker.services.telegram.command.CompletableMessageDataHandler;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;

import java.util.List;

public abstract class NavigationCallbackHandler<T> extends ParsingDataCallbackHandler<T> {

    @Getter(AccessLevel.PROTECTED)
    private final PaginatedKeyboardBuilder keyboardBuilder;

    protected NavigationCallbackHandler(String callbackKey,
                                        CompletableMessageDataHandler handler) {
        super(callbackKey, handler);

        this.keyboardBuilder = new PaginatedKeyboardBuilder((PaginationCallbackParser) getCallbackParser());
    }

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private List<CallbackItem> defaultItems;

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var text = callbackQuery.getData();
        var data = getCallbackParser().parse(text);

        var message = callbackQuery.getMessage();
        var messageData = MessageData.create(message);

        if (data.isEmpty()) {
            executeWithNoArgs(messageData);
            return;
        }
        if (data.isIgnored()) {
            return;
        }

        if (data.isPageNavigation()) {
            navigate(messageData, data);
            return;
        }
        if (data.isSelection()) {
            select(data, messageData);
        }
    }

    @Override
    protected PaginationCallbackParser initCallbackParser() {
        return new PaginationCallbackParser(getKey());
    }

    protected abstract void navigate(MessageData message, CallbackData data);
}