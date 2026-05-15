package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.view.Describable;
import vacancy_tracker.services.telegram.command.handlers.CommandCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;

@Slf4j
@Getter
public abstract class MessageCommand implements Describable, MessageDataHandler {

    private final String key;
    private final String description;

    @Getter(AccessLevel.PROTECTED)
    private final MessagePublisher publisher;

    @Getter(AccessLevel.PROTECTED)
    private final CommandCompletionHandler onComplete;

    protected MessageCommand(String key, String description, MessagePublisher publisher) {
        this.key = key;
        this.description = description;
        this.publisher = publisher;
        this.onComplete = null;
    }

    protected MessageCommand(String key, String description,
                             MessagePublisher publisher, CommandCompletionHandler onComplete) {
        this.key = key;
        this.description = description;
        this.publisher = publisher;
        this.onComplete = onComplete;
    }

    protected abstract void executeAndPopulateMessage(OutgoingMessage messageData);

    public void execute(MessageData message) {
        var outgoingMessage = new OutgoingMessage(message);
        executeAndPopulateMessage(outgoingMessage);
        publisher.publish(outgoingMessage);
    }

    public void endExecution(MessageData message) {
        if (onComplete != null) {
            onComplete.onComplete(this, message);
        }
        log.debug("Команда {} завершила выполнение", this.key);
    }
}
