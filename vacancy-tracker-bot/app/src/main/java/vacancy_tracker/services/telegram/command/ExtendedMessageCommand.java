package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.execution.ExecutionResult;
import vacancy_tracker.services.telegram.SupportsCompletionCheck;
import vacancy_tracker.services.telegram.command.handlers.CommandCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.handlers.ParametrizedDataHandler;

import java.util.concurrent.CompletableFuture;

import static vacancy_tracker.model.telegram.execution.ExecutionFailReason.EXCEPTION;

@Slf4j
@Getter
public abstract class ExtendedMessageCommand<T> extends AbstractMessageCommand implements ParametrizedDataHandler<T>, SupportsCompletionCheck<T> {

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private CommandCompletionHandler onComplete;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private boolean triggerEvent = true;

    protected ExtendedMessageCommand(CommandArgs args,
                                     MessagePublisher publisher,
                                     ExecutionStrategy executionStrategy) {
        super(args, executionStrategy, publisher);
        this.onComplete = args.getCompletionHandler();
    }

    @Override
    public CompletableFuture<ExecutionResult> executeWithCompletionCheck(MessageData messageData, T parameter) {
        var message = new OutgoingMessage(messageData);
        return getExecutionStrategy()
                .executeWithCheck(message.getChatId(), () -> executeWithParameters(message, parameter))
                .thenApply(result -> Boolean.TRUE.equals(result) ?
                        ExecutionResult.success() : ExecutionResult.fail(EXCEPTION));
    }

    @Override
    public final void handleWithParameter(MessageData messageData, T parameter) {
        var chatId = messageData.getChatId();
        getExecutionStrategy().executeWithCheck(chatId, () -> executeWithParameters(messageData, parameter))
                .thenAccept(success -> {
                    if (isTriggerEvent()) {
                        endExecution(messageData, success);
                    }
                });
    }

    protected void endExecution(MessageData message) {
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
