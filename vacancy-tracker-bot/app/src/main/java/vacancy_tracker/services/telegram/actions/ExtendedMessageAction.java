package vacancy_tracker.services.telegram.actions;

import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.execution.ExecutionResult;
import vacancy_tracker.services.telegram.SupportsCompletionCheck;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.handlers.ParametrizedDataHandler;

import java.util.concurrent.CompletableFuture;

import static vacancy_tracker.model.telegram.execution.ExecutionFailReason.EXCEPTION;

//Класс подходит для операций, которые выполняются не долго
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
    public CompletableFuture<ExecutionResult> executeWithCompletionCheck(MessageData messageData, T parameter) {
        var message = new OutgoingMessage(messageData);
        return getExecutionStrategy()
                .executeWithCheck(messageData.getChatId(), () -> executeWithParameters(message, parameter))
                .thenApply(res -> Boolean.TRUE.equals(res) ? ExecutionResult.success() : ExecutionResult.fail(EXCEPTION));
    }

    protected abstract void executeWithParameters(OutgoingMessage messageData, T parameter);
}