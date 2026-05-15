package vacancy_tracker.services.telegram.command.handlers;

import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.MessageCommand;

@FunctionalInterface
public interface CommandCompletionHandler {

    void onComplete(MessageCommand command, MessageData messageData);

}
