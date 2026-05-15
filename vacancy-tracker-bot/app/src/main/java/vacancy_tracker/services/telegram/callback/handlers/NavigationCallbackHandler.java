package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;
import vacancy_tracker.services.telegram.command.MessageDataHandler;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;

public abstract class NavigationCallbackHandler<T> extends ParsingDataCallbackHandler<T> {

    @Getter(AccessLevel.PROTECTED)
    private final PaginatedKeyboardBuilder keyboardBuilder;

    private final MessageEditor messageEditor;

    protected NavigationCallbackHandler(String callbackKey,
                                        MessageDataHandler handler,
                                        MessageEditor messageEditor) {
        super(callbackKey, handler);

        this.keyboardBuilder = new PaginatedKeyboardBuilder((PaginationCallbackParser) getCallbackParser());
        this.messageEditor = messageEditor;
    }

    protected NavigationCallbackHandler(String callbackKey,
                                        PaginatedKeyboardBuilder keyboardBuilder,
                                        MessageDataHandler handler,
                                        MessageEditor messageEditor) {
        super(callbackKey, handler);

        this.keyboardBuilder = keyboardBuilder;
        this.messageEditor = messageEditor;
    }

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

    protected void navigate(MessageData message, CallbackData data) {
        var page = data.targetPage();
        var keyboard = keyboardBuilder.build(page, data.args());
        editKeyboard(message.getChatId(), message.getMessageId(), keyboard);
    }

    protected void editKeyboard(long chatId, int messageId, InlineKeyboardMarkup keyboard) {
        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(keyboard)
                .build();
        messageEditor.edit(editMessageReplyMarkup);
    }
}
