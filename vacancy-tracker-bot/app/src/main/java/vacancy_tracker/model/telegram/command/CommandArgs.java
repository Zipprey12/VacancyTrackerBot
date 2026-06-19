package vacancy_tracker.model.telegram.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import vacancy_tracker.services.telegram.command.handlers.CommandCompletionHandler;

@Data
@AllArgsConstructor
public class CommandArgs {

    private final String key;
    private final String description;
    private final CommandCompletionHandler completionHandler;
}
