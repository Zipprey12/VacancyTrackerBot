package vacancy_tracker.services.telegram.actions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.execution.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.handlers.ExecutableMessageHandler;

@RequiredArgsConstructor
public abstract class MessageAction implements ExecutableMessageHandler {

    @Getter(AccessLevel.PROTECTED)
    private final ExecutionStrategy executionStrategy;

    @Getter(AccessLevel.PROTECTED)
    private final MessagePublisher publisher;

    @Override
    public void execute(MessageData message) {
        var outgoingMessage = new OutgoingMessage(message);
        executionStrategy.execute(
                () -> executeAndPopulateMessage(outgoingMessage),
                () -> {
                    if (outgoingMessage.getText() != null) {
                        publisher.publish(outgoingMessage);
                    }
                }
        );
    }

    protected abstract void executeAndPopulateMessage(OutgoingMessage messageData);
}
