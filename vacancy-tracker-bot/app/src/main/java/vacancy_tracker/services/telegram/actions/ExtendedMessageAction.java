package vacancy_tracker.services.telegram.actions;

import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.SupportsCompletionCheck;
import vacancy_tracker.services.telegram.command.execution.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.handlers.ParametrizedDataHandler;

import java.util.concurrent.CompletableFuture;

public abstract class ExtendedMessageAction<T> extends MessageAction implements ParametrizedDataHandler<T>, SupportsCompletionCheck<T> {

    protected ExtendedMessageAction(ExecutionStrategy executionStrategy, MessagePublisher publisher) {
        super(executionStrategy, publisher);
    }

    @Override
    public final void handleWithParameter(MessageData messageData, T parameters) {
        var message = new OutgoingMessage(messageData);
        getExecutionStrategy().execute(() -> executeWithParameters(message, parameters));
    }

    @Override
    public CompletableFuture<Boolean> executeWithCompletionCheck(MessageData messageData, T parameter) {
        var message = new OutgoingMessage(messageData);
        return getExecutionStrategy().executeWithCheck(() -> executeWithParameters(message, parameter));
    }

    protected abstract void executeAndPopulateMessage(OutgoingMessage outgoingMessage);

    protected abstract void executeWithParameters(OutgoingMessage messageData, T parameter);
}