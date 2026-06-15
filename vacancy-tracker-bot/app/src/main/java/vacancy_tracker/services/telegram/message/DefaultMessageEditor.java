package vacancy_tracker.services.telegram.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultMessageEditor implements MessageEditor {

    private final TelegramClient client;

    @Override
    public boolean edit(EditMessageText editText) {
        return tryExecute(editText, editText.getChatId(), editText.getMessageId());
    }

    @Override
    public boolean edit(InlineKeyboardMarkup inlineKeyboardMarkup, long chatId, int messageId) {
        var editMarkup = EditMessageReplyMarkup.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(chatId)
                .messageId(messageId)
                .build();
        return tryExecute(editMarkup, String.valueOf(chatId), messageId);
    }

    @Override
    public boolean edit(OutgoingMessage message) {
        var editMessage = EditMessageText.builder()
                .messageId(message.getMessageId())
                .chatId(message.getChatId())
                .text(message.getText())
                .parseMode(message.getParseMode())
                .replyMarkup(message.getKeyboardMarkup())
                .build();
        return edit(editMessage);
    }

    private boolean tryExecute(BotApiMethod<?> method, String chatId, int messageId) {
        try {
            client.execute(method);
            return true;
        } catch (Exception e) {
            handleException(e, chatId, messageId);
            return false;
        }
    }

    private void handleException(Exception e, String chatId, int messageId) {
        log.error("Ошибка при изменении сообщения: {} в чате {}", messageId, chatId, e);
    }
}
