package vacancy_tracker.services.telegram.events;

import lombok.Getter;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;

@Getter
public class NotificationSettingEvent extends CommandExecutionEvent<Identifiable> {

    private final transient NotificationSettings settings;

    public NotificationSettingEvent(Identifiable source,
                                    MessageData messageData,
                                    NotificationSettings settings) {
        super(source, messageData);
        this.settings = settings;
    }
}
