package vacancy_tracker.services.telegram.command.execution.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class AsyncExecutionStrategy implements ExecutionStrategy {

    private final Executor taskExecutor;

    @Override
    public void execute(Runnable populateMessage, Runnable publish) {
        CompletableFuture
                .runAsync(populateMessage, taskExecutor)
                .thenRun(publish)
                .exceptionally(e -> {
                    log.error("Ошибка асинхронного выполнения команды", e);
                    return null;
                });
    }

    @Override
    public void execute(Runnable execute) {
        CompletableFuture
                .runAsync(execute, taskExecutor)
                .exceptionally(e -> {
                    log.error("Ошибка асинхронного выполнения операции", e);
                    return null;
                });
    }

    @Override
    public CompletableFuture<Boolean> executeWithCheck(Runnable execute) {
        try {
            execute.run();
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Ошибка выполнения", e);
            return CompletableFuture.completedFuture(false);
        }
    }
}
