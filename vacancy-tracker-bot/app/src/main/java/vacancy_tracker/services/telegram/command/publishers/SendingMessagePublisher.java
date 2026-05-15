package vacancy_tracker.services.telegram.command.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageSender;

@Component
@RequiredArgsConstructor
public class SendingMessagePublisher implements MessagePublisher {

    private final MessageSender sender;

    public final void publish(OutgoingMessage message) {
        send(message);
    }

    protected void send(OutgoingMessage messageData) {
        sender.send(messageData);
    }
}