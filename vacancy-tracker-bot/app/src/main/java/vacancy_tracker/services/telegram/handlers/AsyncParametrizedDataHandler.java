package vacancy_tracker.services.telegram.handlers;

import vacancy_tracker.model.telegram.dto.MessageData;

import java.util.concurrent.CompletableFuture;


public interface AsyncParametrizedDataHandler<T> extends ParametrizedDataHandler<T> {

    CompletableFuture<Void> executeAsync(MessageData messageData);

    CompletableFuture<Void> handleWithParameterAsync(MessageData messageData, T parameters);
}
