package vacancy_tracker.services.telegram.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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
    public void edit(EditMessageText editMessageText) {
        try {
            client.execute(editMessageText);
        } catch (Exception e) {
            var messageId = editMessageText.getMessageId();
            var chatId = editMessageText.getChatId();
            handleException(e, chatId, messageId);
        }
    }

    @Override
    public void edit(EditMessageReplyMarkup editMessageReplyMarkup) {
        try {
            client.execute(editMessageReplyMarkup);
        } catch (Exception e) {
            var messageId = editMessageReplyMarkup.getMessageId();
            var chatId = editMessageReplyMarkup.getChatId();
            handleException(e, chatId, messageId);
        }
    }

    @Override
    public void edit(String text, long chatId, int messageId) {
        var editMessage = EditMessageText.builder()
                .messageId(messageId)
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
        try {
            client.execute(editMessage);
        } catch (Exception e) {
            handleException(e, chatId, messageId);
        }
    }

    @Override
    public void edit(InlineKeyboardMarkup inlineKeyboardMarkup, long chatId, int messageId) {
        var editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(chatId)
                .messageId(messageId)
                .build();
        try {
            client.execute(editMessageReplyMarkup);
        } catch (Exception e) {
            handleException(e, chatId, messageId);
        }
    }

    @Override
    public void edit(OutgoingMessage commandMessageData) {
        var editMessage = EditMessageText.builder()
                .messageId(commandMessageData.getMessageId())
                .chatId(commandMessageData.getChatId())
                .text(commandMessageData.getText())
                .parseMode(commandMessageData.getParseMode())
                .build();

        edit(editMessage);

        var keyboard = commandMessageData.getKeyboardMarkup();
        if (keyboard != null) {
            var editMarkup = EditMessageReplyMarkup.builder()
                    .messageId(commandMessageData.getMessageId())
                    .chatId(commandMessageData.getChatId())
                    .replyMarkup(keyboard)
                    .build();
            edit(editMarkup);
        }
    }

    private void handleException(Exception e, String chatId, int messageId) {
        log.error("Ошибка при изменении сообщения: {} в чате {}", messageId, chatId, e);
    }

    private void handleException(Exception e, long chatId, int messageId) {
        log.error("Ошибка при изменении сообщения: {} в чате {}", messageId, chatId, e);
    }
}
