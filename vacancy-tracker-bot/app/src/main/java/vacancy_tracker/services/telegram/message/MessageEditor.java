package vacancy_tracker.services.telegram.message;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;

public interface MessageEditor {

    boolean edit(EditMessageText editMessageText);

    boolean edit(OutgoingMessage commandMessageData);
}
