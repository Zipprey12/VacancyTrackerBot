package vacancy_tracker.services.telegram.events;

import vacancy_tracker.model.telegram.MessageData;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;

public class SettingCommandExecutionEvent extends CommandExecutionEvent<InputInterceptingCommand> {

    public SettingCommandExecutionEvent(InputInterceptingCommand source, MessageData messageData) {
        super(source, messageData);
    }
}
