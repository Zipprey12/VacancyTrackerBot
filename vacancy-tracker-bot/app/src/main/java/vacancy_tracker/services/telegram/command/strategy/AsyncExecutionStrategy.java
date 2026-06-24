package vacancy_tracker.services.telegram.command.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class AsyncExecutionStrategy implements ExecutionStrategy {

    public static final String OPERATION_EXECUTION_ERROR = "Ошибка асинхронного выполнения операции";
    public static final String COMMAND_EXECUTION_ERROR = "Ошибка асинхронного выполнения команды";

    private final Executor taskExecutor;

    @Override
    public void execute(long chatId, Runnable populateMessage, Runnable publish) {
        CompletableFuture
                .runAsync(populateMessage, taskExecutor)
                .thenRun(publish)
                .exceptionally(e -> {
                    log.error(COMMAND_EXECUTION_ERROR, e);
                    return null;
                });
    }

    @Override
    public void execute(long chatId, Runnable execute) {
        CompletableFuture
                .runAsync(execute, taskExecutor)
                .exceptionally(e -> {
                    log.error(OPERATION_EXECUTION_ERROR, e);
                    return null;
                });
    }

    @Override
    public CompletableFuture<Boolean> executeWithCheck(long chatId, Runnable execute) {
        return CompletableFuture
                .runAsync(execute, taskExecutor)
                .thenApply(v -> true)
                .exceptionally(e -> {
                    log.error(OPERATION_EXECUTION_ERROR, e);
                    return false;
                });
    }
}
