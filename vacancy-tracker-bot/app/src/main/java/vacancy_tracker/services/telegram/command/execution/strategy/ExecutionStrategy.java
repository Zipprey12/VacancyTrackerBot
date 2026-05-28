package vacancy_tracker.services.telegram.command.execution.strategy;

import java.util.concurrent.CompletableFuture;

public interface ExecutionStrategy {

    void execute(Runnable populate, Runnable publish);

    void execute(Runnable runnable);

    CompletableFuture<Boolean> executeWithCheck(Runnable execute);

    static ExecutionStrategy sync() {
        return new SyncExecutionStrategy();
    }
}