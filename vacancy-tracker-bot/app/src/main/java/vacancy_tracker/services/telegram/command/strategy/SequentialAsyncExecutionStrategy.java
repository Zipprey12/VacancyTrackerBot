package vacancy_tracker.services.telegram.command.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class SequentialAsyncExecutionStrategy implements ExecutionStrategy {

    private final Executor taskExecutor;
    private final ConcurrentHashMap<Long, CompletableFuture<Void>> executionChains = new ConcurrentHashMap<>();

    @Override
    public void execute(long chatId, Runnable runnable) {
        executionChains.compute(chatId, (id, current) -> {
            var next = (current == null ? CompletableFuture.completedFuture((Void) null) : current)
                    .thenRunAsync(runnable, taskExecutor)
                    .exceptionally(e -> {
                        log.error("Ошибка последовательного асинхронного выполнения chatId={}", chatId, e);
                        return null;
                    });
            next.thenRun(() -> executionChains.remove(chatId, next));
            return next;
        });
    }

    @Override
    public void execute(long chatId, Runnable populate, Runnable publish) {
        executionChains.compute(chatId, (id, current) -> {
            var next = (current == null ? CompletableFuture.completedFuture((Void) null) : current)
                    .thenRunAsync(populate, taskExecutor)
                    .thenRun(publish)
                    .exceptionally(e -> {
                        log.error("Ошибка последовательного асинхронного выполнения chatId={}", chatId, e);
                        return null;
                    });
            next.thenRun(() -> executionChains.remove(chatId, next));
            return next;
        });
    }

    @Override
    public CompletableFuture<Boolean> executeWithCheck(long chatId, Runnable execute) {
        var result = new CompletableFuture<Boolean>();

        executionChains.compute(chatId, (id, current) -> {
            var next = (current == null ? CompletableFuture.completedFuture((Void) null) : current)
                    .thenRunAsync(execute, taskExecutor)
                    .thenRun(() -> result.complete(true))
                    .exceptionally(e -> {
                        log.error("Ошибка последовательного выполнения chatId={}", chatId, e);
                        result.complete(false);
                        return null;
                    });
            next.thenRun(() -> executionChains.remove(chatId, next));
            return next;
        });

        return result;
    }
}
