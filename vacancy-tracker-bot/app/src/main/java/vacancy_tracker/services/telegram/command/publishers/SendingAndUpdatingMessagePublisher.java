package vacancy_tracker.services.telegram.command.publishers;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionMessagesService;

import static vacancy_tracker.model.telegram.session.PublishType.SEND;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class SendingAndUpdatingMessagePublisher implements MessagePublisher {

    private final SessionMessagesService messagesService;
    private final MessageEditor editor;
    private final MessageSender sender;

    @Override
    public final Integer publish(OutgoingMessage message) {
        if (message.getSource().equals(SEND)) {
            return send(message);

        } else {
            if (message.isSendIfNotLast()) {
                var isLast = messagesService.isLast(message.getChatId(), message.getMessageId());
                if (!isLast) {
                    return send(message);
                }
            }
            return edit(message);
        }
    }

    protected Integer edit(OutgoingMessage messageData) {
        log.debug("Called edit message {} in chat {}", messageData.getMessageId(), messageData.getChatId());
        editor.edit(messageData);
        return messageData.getMessageId();
    }

    protected Integer send(OutgoingMessage messageData) {
        log.debug("Called send message {} to chat {}", messageData.getMessageId(), messageData.getChatId());
        var messageId = sender.send(messageData);
        if (messageId != null) {
            messagesService.saveLast(messageData.getChatId(), messageId);
        }
        return messageId;
    }
}
