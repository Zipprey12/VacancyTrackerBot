package vacancy_tracker.services.telegram.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;

@Getter
public class CommandExecutionEvent<T extends Identifiable> extends ApplicationEvent {

    private final transient T identifiableSource;

    private final transient MessageData messageData;

    public CommandExecutionEvent(T source, MessageData messageData) {
        super(source);
        this.identifiableSource = source;
        this.messageData = messageData;
    }
}
