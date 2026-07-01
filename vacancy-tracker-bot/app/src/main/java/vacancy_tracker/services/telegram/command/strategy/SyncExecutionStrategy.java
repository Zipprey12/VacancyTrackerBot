package vacancy_tracker.services.telegram.command.strategy;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class SyncExecutionStrategy implements ExecutionStrategy {

    @Override
    public void execute(long chatId, Runnable populateMessage, Runnable publish) {
        populateMessage.run();
        publish.run();
    }

    @Override
    public void execute(long chatId, Runnable execute) {
        execute.run();
    }

    @Override
    public CompletableFuture<Boolean> executeWithCheck(long chatId, Runnable execute) {
        try {
            execute.run();
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Ошибка выполнения", e);
            return CompletableFuture.completedFuture(false);
        }
    }
}
