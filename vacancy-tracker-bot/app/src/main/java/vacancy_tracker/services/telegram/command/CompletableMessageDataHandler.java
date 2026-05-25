package vacancy_tracker.services.telegram.command;

import vacancy_tracker.model.telegram.dto.MessageData;

public interface CompletableMessageDataHandler extends MessageDataHandler {
    void endExecution(MessageData messageData, boolean isSuccess);

    void endExecution(MessageData messageData);
}
