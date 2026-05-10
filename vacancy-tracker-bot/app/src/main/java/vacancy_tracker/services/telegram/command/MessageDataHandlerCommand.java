package vacancy_tracker.services.telegram.command;

import vacancy_tracker.model.telegram.dto.MessageData;

public interface MessageDataHandlerCommand {

    void handleData(MessageData messageData, boolean shouldOverwrite);
}
