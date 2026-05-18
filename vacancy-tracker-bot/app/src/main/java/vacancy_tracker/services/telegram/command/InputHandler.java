package vacancy_tracker.services.telegram.command;

import vacancy_tracker.model.telegram.dto.MessageData;

public interface InputHandler<T> {
    void handleWithParameter(MessageData messageData, T parameters);

    void handleInvalidValue(MessageData messageData);
}
