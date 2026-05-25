package vacancy_tracker.services.telegram.command;

import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;

public interface InputHandler<T> extends Identifiable {

    void handleWithParameter(MessageData messageData, T parameters);

    void handleInvalidValue(MessageData messageData);
}
