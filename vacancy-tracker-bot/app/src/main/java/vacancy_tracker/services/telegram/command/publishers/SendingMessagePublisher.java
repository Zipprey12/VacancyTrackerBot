package vacancy_tracker.services.telegram.command.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionMessagesService;

@Component
@RequiredArgsConstructor
public class SendingMessagePublisher implements MessagePublisher {

    private final MessageSender sender;
    private final SessionMessagesService messagesService;

    public final Integer publish(OutgoingMessage message) {
        return send(message);
    }

    protected Integer send(OutgoingMessage messageData) {
        var messageId = sender.send(messageData);
        if (messageId != null) {
            messagesService.saveLast(messageData.getChatId(), messageId);
        }
        return messageId;
    }
}