package vacancy_tracker.services.telegram.events;

import lombok.Getter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;

@Getter
public class NotificationSettingEvent extends CommandExecutionEvent<Identifiable> {

    public NotificationSettingEvent(Identifiable source,
                                    MessageData messageData) {
        super(source, messageData);
    }
}
