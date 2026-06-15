package vacancy_tracker.services.telegram.command.strategy;

import java.util.concurrent.CompletableFuture;

public interface ExecutionStrategy {

    static ExecutionStrategy sync() {
        return new SyncExecutionStrategy();
    }

    void execute(Runnable populate, Runnable publish);

    void execute(Runnable runnable);

    CompletableFuture<Boolean> executeWithCheck(Runnable execute);

    default void execute(long chatId, Runnable runnable) {
        execute(runnable);
    }

    default void execute(long chatId, Runnable populate, Runnable publish) {
        execute(populate, publish);
    }

    default CompletableFuture<Boolean> executeWithCheck(long chatId, Runnable execute) {
        return executeWithCheck(execute);
    }
}