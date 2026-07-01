package vacancy_tracker.services.telegram.actions;

import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.execution.ExecutionFailReason;
import vacancy_tracker.model.telegram.execution.ExecutionResult;
import vacancy_tracker.services.telegram.SupportsCompletionCheck;
import vacancy_tracker.services.telegram.handlers.AsyncParametrizedDataHandler;

import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AsyncAction<T> implements AsyncParametrizedDataHandler<T>, SupportsCompletionCheck<T> {

    public static final String ERROR_MESSAGE = "Ошибка при выполнении асинхронной операции!";

    @Override
    public CompletableFuture<ExecutionResult> executeWithCompletionCheck(MessageData messageData, T parameter) {
        return handleWithParameterAsync(messageData, parameter)
                .thenApply(v -> ExecutionResult.success())
                .exceptionally(e -> {
                    log.error(ERROR_MESSAGE, e);
                    return ExecutionResult.fail(ExecutionFailReason.EXCEPTION);
                });
    }

    @Override
    public void execute(MessageData messageData) {
        fireAndForget(executeAsync(messageData));
    }

    @Override
    public void handleWithParameter(MessageData messageData, T parameters) {
        fireAndForget(handleWithParameterAsync(messageData, parameters));
    }

    protected void fireAndForget(CompletableFuture<Void> future) {
        future.exceptionally(e -> {
            log.error(ERROR_MESSAGE, e);
            return null;
        });
    }
}
