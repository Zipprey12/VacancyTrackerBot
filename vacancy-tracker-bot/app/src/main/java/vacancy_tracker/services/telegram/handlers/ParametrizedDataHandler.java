package vacancy_tracker.services.telegram.handlers;

import vacancy_tracker.model.telegram.dto.MessageData;

public interface ParametrizedDataHandler<T> extends ExecutableMessageHandler {

    void handleWithParameter(MessageData messageData, T parameters);
}
