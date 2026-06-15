package vacancy_tracker.services.telegram.command;

import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;

public abstract class SimpleMessageCommand extends AbstractMessageCommand {

    protected SimpleMessageCommand(String key, String description, MessagePublisher publisher) {
        this(key, description, ExecutionStrategy.sync(), publisher);
    }

    protected SimpleMessageCommand(String key, String description,
                                   ExecutionStrategy executionStrategy,
                                   MessagePublisher publisher) {
        super(key, description, executionStrategy, publisher);
    }
}