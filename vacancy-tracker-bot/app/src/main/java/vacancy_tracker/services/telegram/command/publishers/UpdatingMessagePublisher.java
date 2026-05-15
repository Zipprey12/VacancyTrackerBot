package vacancy_tracker.services.telegram.command.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageEditor;

@Component
@RequiredArgsConstructor
public class UpdatingMessagePublisher implements MessagePublisher {

    private final MessageEditor editor;

    public final void publish(OutgoingMessage message) {
        update(message);
    }

    protected void update(OutgoingMessage messageData) {
        editor.edit(messageData);
    }
}
