package vacancy_tracker.services.telegram.handlers;

import vacancy_tracker.model.telegram.dto.MessageData;

public interface ExecutableMessageHandler {

    void execute(MessageData messageData);

}
