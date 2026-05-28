package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.SupportsCompletionCheck;
import vacancy_tracker.services.telegram.command.execution.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.command.handlers.CommandCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.handlers.ParametrizedDataHandler;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Getter
public abstract class ExtendedMessageCommand<T> extends AbstractMessageCommand implements ParametrizedDataHandler<T>, SupportsCompletionCheck<T> {

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private CommandCompletionHandler onComplete;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private boolean triggerEvent = true;

    private final ExecutionStrategy executionStrategy;

    protected ExtendedMessageCommand(String key, String description,
                                     MessagePublisher publisher,
                                     CommandCompletionHandler handler) {
        this(key, description, publisher, ExecutionStrategy.sync(), handler);
    }

    protected ExtendedMessageCommand(String key, String description, MessagePublisher publisher) {
        this(key, description, publisher, ExecutionStrategy.sync(), null);
    }

    protected ExtendedMessageCommand(String key, String description,
                                     MessagePublisher publisher,
                                     ExecutionStrategy executionStrategy,
                                     CommandCompletionHandler handler) {
        super(key, description, executionStrategy, publisher);
        this.onComplete = handler;
        this.executionStrategy = ExecutionStrategy.sync();
    }

    @Override
    public CompletableFuture<Boolean> executeWithCompletionCheck(MessageData messageData, T parameter) {
        var message = new OutgoingMessage(messageData);
        return getExecutionStrategy().executeWithCheck(() -> executeWithParameters(message, parameter));
    }

    @Override
    public final void handleWithParameter(MessageData messageData, T parameter) {
        executionStrategy.execute(() -> executeWithParameters(messageData, parameter));
        if (triggerEvent) {
            endExecution(messageData);
        }
    }

    protected void endExecution(MessageData message) {
        log.debug("{} - завершение работы", getKey());
        endExecution(message, true);
    }

    protected void endExecution(MessageData message, boolean isSuccess) {
        if (onComplete != null && isSuccess) {
            onComplete.onComplete(this, message);
        }
        log.debug("Выполнение команды {} завершено", getKey());
    }

    protected abstract void executeWithParameters(MessageData messageData, T parameter);
}
