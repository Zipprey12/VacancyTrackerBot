package vacancy_tracker.services.telegram.command;

import vacancy_tracker.model.telegram.MessageData;

public interface MessageDataHandler {

    void execute(MessageData messageData);
}
