package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.view.Describable;
import vacancy_tracker.services.telegram.command.handlers.CommandCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;

@Slf4j
@Getter
public abstract class CompletableMessageCommand implements Describable, CompletableMessageDataHandler {

    private final String key;
    private final String description;

    @Getter(AccessLevel.PROTECTED)
    private final MessagePublisher publisher;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private CommandCompletionHandler onComplete;

    protected CompletableMessageCommand(String key, String description, MessagePublisher publisher) {
        this.key = key;
        this.description = description;
        this.publisher = publisher;
        this.onComplete = null;
    }

    protected CompletableMessageCommand(String key, String description,
                                        MessagePublisher publisher,
                                        CommandCompletionHandler onComplete) {
        this.key = key;
        this.description = description;
        this.publisher = publisher;
        this.onComplete = onComplete;
    }

    protected abstract void executeAndPopulateMessage(OutgoingMessage messageData);

    @Override
    public void execute(MessageData message) {
        var outgoingMessage = new OutgoingMessage(message);
        executeAndPopulateMessage(outgoingMessage);
        publisher.publish(outgoingMessage);
    }

    public void endExecution(MessageData message) {
        if (onComplete != null) {
            onComplete.onComplete(this, message);
        }
        log.debug("Выполнение команды {} завершено", this.key);
    }
}
