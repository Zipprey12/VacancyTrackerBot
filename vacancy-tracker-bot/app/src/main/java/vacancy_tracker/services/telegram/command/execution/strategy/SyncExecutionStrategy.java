package vacancy_tracker.services.telegram.command.execution.strategy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class SyncExecutionStrategy implements ExecutionStrategy {

    @Override
    public void execute(Runnable populateMessage, Runnable publish) {
        populateMessage.run();
        publish.run();
    }

    @Override
    public void execute(Runnable execute) {
        execute.run();
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
