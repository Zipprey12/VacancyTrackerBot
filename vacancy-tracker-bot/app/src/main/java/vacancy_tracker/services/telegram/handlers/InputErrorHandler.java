package vacancy_tracker.services.telegram.handlers;

import vacancy_tracker.model.telegram.dto.MessageData;

public interface InputErrorHandler {

    void handleInvalidValue(MessageData messageData);

    void handleInvalidValue(MessageData messageData, String reason);
}
