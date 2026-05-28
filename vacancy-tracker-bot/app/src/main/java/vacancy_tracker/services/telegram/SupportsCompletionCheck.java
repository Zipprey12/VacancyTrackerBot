package vacancy_tracker.services.telegram;

import vacancy_tracker.model.telegram.dto.MessageData;

import java.util.concurrent.CompletableFuture;

public interface SupportsCompletionCheck<T> {

    CompletableFuture<Boolean> executeWithCompletionCheck(MessageData messageData, T parameter);
}
