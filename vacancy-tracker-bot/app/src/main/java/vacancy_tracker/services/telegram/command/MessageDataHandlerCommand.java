package vacancy_tracker.services.telegram.command;

import vacancy_tracker.model.telegram.dto.MessageData;

public interface MessageDataHandlerCommand {

    void execute(MessageData messageData, boolean shouldOverwrite);

    void handleExecutionEnd(MessageData messageData, boolean isInterceptorUsed);
}
