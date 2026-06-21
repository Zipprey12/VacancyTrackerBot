package vacancy_tracker.services.telegram.command.publishers;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;

import static vacancy_tracker.model.telegram.session.PublishType.UPDATE;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class SendingAndUpdatingMessagePublisher implements MessagePublisher {

    private final MessageEditor editor;
    private final MessageSender sender;

    @Override
    public final void publish(OutgoingMessage message) {
        if (message.getSource().equals(UPDATE)) {
            edit(message);
        } else {
            send(message);
        }
    }

    protected void edit(OutgoingMessage messageData) {
        log.debug("Called edit message {} in chat {}", messageData.getMessageId(), messageData.getChatId());
        editor.edit(messageData);
    }

    protected void send(OutgoingMessage messageData) {
        log.debug("Called send message {} to chat {}", messageData.getMessageId(), messageData.getChatId());
        sender.send(messageData);
    }
}
