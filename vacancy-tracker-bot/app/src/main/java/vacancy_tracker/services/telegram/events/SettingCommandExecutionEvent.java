package vacancy_tracker.services.telegram.events;

import lombok.Getter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;

public class SettingCommandExecutionEvent extends CommandExecutionEvent<InputInterceptingCommand> {

    @Getter
    private final boolean isInterceptorUsed;

    public SettingCommandExecutionEvent(InputInterceptingCommand source, MessageData messageData, boolean isInterceptorUsed) {
        super(source, messageData);
        this.isInterceptorUsed = isInterceptorUsed;
    }
}
