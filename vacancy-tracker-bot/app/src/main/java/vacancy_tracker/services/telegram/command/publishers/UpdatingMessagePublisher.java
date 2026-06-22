package vacancy_tracker.services.telegram.command.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageEditor;

@Component
@RequiredArgsConstructor
public class UpdatingMessagePublisher implements MessagePublisher {

    private final MessageEditor editor;

    public final Integer publish(OutgoingMessage message) {
        return update(message);
    }

    protected Integer update(OutgoingMessage messageData) {
        editor.edit(messageData);
        return messageData.getMessageId();
    }
}
