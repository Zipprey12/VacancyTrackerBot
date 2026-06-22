package vacancy_tracker.services.telegram.command.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageSender;

@Component
@RequiredArgsConstructor
public class SendingMessagePublisher implements MessagePublisher {

    private final MessageSender sender;

    public final Integer publish(OutgoingMessage message) {
        return send(message);
    }

    protected Integer send(OutgoingMessage messageData) {
        return sender.send(messageData);
    }
}