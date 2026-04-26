package vacancy_tracker.services.telegram.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultMessageEditor {

    private final TelegramClient client;

    public void edit(EditMessageText editMessageText){
        try {
            client.execute(editMessageText);
        }
        catch (TelegramApiException e){
            var messageId = editMessageText.getMessageId();
            var chatId = editMessageText.getChatId();
            log.warn("Ошибка при изменении сообщения: {} в чате {}", messageId, chatId, e);
        }
    }

    public void edit(EditMessageReplyMarkup editMessageReplyMarkup){
        try {
            client.execute(editMessageReplyMarkup);
        }
        catch (TelegramApiException e){
            var messageId = editMessageReplyMarkup.getMessageId();
            var chatId = editMessageReplyMarkup.getChatId();
            log.warn("Ошибка при изменении сообщения: {} в чате {}", messageId, chatId, e);
        }
    }
}
