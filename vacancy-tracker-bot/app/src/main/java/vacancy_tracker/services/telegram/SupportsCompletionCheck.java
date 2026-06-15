package vacancy_tracker.services.telegram;

import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.execution.ExecutionResult;

import java.util.concurrent.CompletableFuture;

public interface SupportsCompletionCheck<T> {

    CompletableFuture<ExecutionResult> executeWithCompletionCheck(MessageData messageData, T parameter);
}
