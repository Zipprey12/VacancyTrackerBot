package vacancy_tracker.services.telegram.command.handlers;

import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;

@FunctionalInterface
public interface CommandCompletionHandler {

    void onComplete(Identifiable command, MessageData messageData);
}
