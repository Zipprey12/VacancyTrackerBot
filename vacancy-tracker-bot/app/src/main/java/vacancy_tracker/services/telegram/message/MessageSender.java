package vacancy_tracker.services.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;

public interface MessageSender {

    void sendText(long chatId, String text);

    void sendText(long chatId, String text, String parseMode);

    void send(SendMessage sendMessage);

    void sendInvalidValueError(long chatId);

    void answerCallback(String callbackId);

    void send(OutgoingMessage commandMessageData);
}
