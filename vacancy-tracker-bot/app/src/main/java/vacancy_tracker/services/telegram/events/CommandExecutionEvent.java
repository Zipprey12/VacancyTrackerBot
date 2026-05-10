package vacancy_tracker.services.telegram.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.MessageBotCommand;

@Getter
public class CommandExecutionEvent<T extends MessageBotCommand> extends ApplicationEvent {

    private final transient T command;

    private final transient MessageData messageData;

    public CommandExecutionEvent(T command, MessageData messageData) {
        super(command);
        this.command = command;
        this.messageData = messageData;
    }
}
