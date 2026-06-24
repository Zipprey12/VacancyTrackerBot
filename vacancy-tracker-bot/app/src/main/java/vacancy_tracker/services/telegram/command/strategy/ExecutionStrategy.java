package vacancy_tracker.services.telegram.command.strategy;

import java.util.concurrent.CompletableFuture;

public interface ExecutionStrategy {

    static ExecutionStrategy sync() {
        return new SyncExecutionStrategy();
    }

    void execute(long chatId, Runnable runnable);


    void execute(long chatId, Runnable populate, Runnable publish);

    CompletableFuture<Boolean> executeWithCheck(long chatId, Runnable execute);
}