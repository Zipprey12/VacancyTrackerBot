package vacancy_tracker.services.telegram.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultMessageSender implements MessageSender {

    private final TelegramClient client;

    @Override
    public void sendText(long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        send(sendMessage);
    }

    @Override
    public void sendText(long chatId, String text, String parseMode) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(parseMode)
                .build();
        send(sendMessage);
    }

    @Override
    public void send(SendMessage sendMessage) {
        try {
            client.execute(sendMessage);
        }
        catch (TelegramApiException e){
            var text = sendMessage.getText();
            var chatId = sendMessage.getChatId();

            log.warn("Ошибка отправки сообщения: {} в чат {}", text, chatId, e);
        }
    }

    //todo
    @Override
    public void sendInvalidValueError(long chatId) {
        sendText(chatId, "Неверный формат данных");
    }

    @Override
    public void answerCallback(String callbackId) {
        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .build();

        try {
            client.execute(answer);
        } catch (TelegramApiException e) {
            log.warn("Ошибка при ответе на callback {}", answer.getCallbackQueryId(), e);
        }
    }

    @Override
    public void send(OutgoingMessage commandMessageData) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(commandMessageData.getChatId())
                .text(commandMessageData.getText())
                .parseMode(commandMessageData.getParseMode())
                .replyMarkup(commandMessageData.getKeyboardMarkup())
                .build();
        send(sendMessage);
    }
}
