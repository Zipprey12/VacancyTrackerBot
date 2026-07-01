package vacancy_tracker.services.telegram.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;

public interface MessageSender {

    Integer send(SendMessage sendMessage);

    void answerCallback(String callbackId);

    Integer send(OutgoingMessage commandMessageData);
}
