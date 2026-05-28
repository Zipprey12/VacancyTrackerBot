package vacancy_tracker.services.telegram.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.services.telegram.actions.MessageAction;
import vacancy_tracker.services.telegram.command.execution.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;

@Slf4j
@Getter
public abstract class AbstractMessageCommand extends MessageAction implements MessageCommand {

    private final String key;
    private final String description;

    protected AbstractMessageCommand(String key, String description,
                                     ExecutionStrategy executionStrategy,
                                     MessagePublisher publisher) {
        super(executionStrategy, publisher);
        this.key = key;
        this.description = description;
    }
}