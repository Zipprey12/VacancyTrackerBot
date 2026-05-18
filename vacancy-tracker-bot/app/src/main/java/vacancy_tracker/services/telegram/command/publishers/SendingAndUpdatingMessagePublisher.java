package vacancy_tracker.services.telegram.command.publishers;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;

import static vacancy_tracker.model.telegram.CallingSource.CALLBACK;

@Getter
@Component
@RequiredArgsConstructor
public class SendingAndUpdatingMessagePublisher implements MessagePublisher {

    private final MessageEditor editor;
    private final MessageSender sender;

    @Override
    public final void publish(OutgoingMessage message) {
        if (message.getSource().equals(CALLBACK)) {
            edit(message);
        } else {
            send(message);
        }
    }

    protected void edit(OutgoingMessage messageData) {
        editor.edit(messageData);
    }

    protected void send(OutgoingMessage messageData) {
        sender.send(messageData);
    }
}
