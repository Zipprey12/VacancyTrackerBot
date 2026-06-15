package vacancy_tracker.services.telegram.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.services.telegram.actions.MessageAction;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;

@Slf4j
@Getter
public abstract class AbstractMessageCommand extends MessageAction implements MessageCommand {

    private final String key;
    private final String description;

    protected AbstractMessageCommand(String key, String description,
                                     ExecutionStrategy executionStrategy,
                                     MessagePublisher publisher) {
        super(executionStrategy == null ? ExecutionStrategy.sync() : executionStrategy, publisher);
        this.key = key;
        this.description = description;
    }
}