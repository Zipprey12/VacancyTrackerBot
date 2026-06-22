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
public class MessageSenderImpl implements MessageSender {

    private final TelegramClient client;

    @Override
    public Integer send(SendMessage sendMessage) {
        try {
            var result = client.execute(sendMessage);
            return result.getMessageId();
        } catch (TelegramApiException e) {
            var text = sendMessage.getText();
            var chatId = sendMessage.getChatId();

            log.warn("Ошибка отправки сообщения: {} в чат {}", text, chatId, e);
            return null;
        }
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
    public Integer send(OutgoingMessage commandMessageData) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(commandMessageData.getChatId())
                .text(commandMessageData.getText())
                .parseMode(commandMessageData.getParseMode())
                .replyMarkup(commandMessageData.getKeyboardMarkup())
                .build();
        return send(sendMessage);
    }
}
