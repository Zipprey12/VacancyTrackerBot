package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.PaginationCallbackData;
import vacancy_tracker.services.telegram.callback.PaginationCallbackParser;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;

public abstract class NavigationCallbackHandler extends CallbackHandler {

    @Getter(AccessLevel.PROTECTED)
    private final PaginatedKeyboardBuilder keyboardBuilder;

    private final PaginationCallbackParser parser;
    private final MessageEditor messageEditor;

    protected NavigationCallbackHandler(String callbackKey,
                                        PaginatedKeyboardBuilder keyboardBuilder,
                                        MessageEditor messageEditor) {
        super(callbackKey);

        this.keyboardBuilder = keyboardBuilder;
        this.parser = keyboardBuilder.getParser();
        this.messageEditor = messageEditor;
    }

    protected abstract void select(PaginationCallbackData data, MessageData messageData);
    protected abstract void executeWithNoArgs(MessageData messageData);

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var text = callbackQuery.getData();
        var data = parser.parse(text);

        var message = callbackQuery.getMessage();
        var messageData = MessageData.create(message);

        if(data.isEmpty()){
            executeWithNoArgs(messageData);
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

    protected void navigate(MessageData message, PaginationCallbackData data) {
        var page = data.getTargetPage();
        var keyboard = keyboardBuilder.build(page, data.getArgs());
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
