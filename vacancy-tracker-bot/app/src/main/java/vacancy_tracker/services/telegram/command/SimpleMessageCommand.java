package vacancy_tracker.services.telegram.command;

import vacancy_tracker.services.telegram.command.execution.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;

public abstract class SimpleMessageCommand extends AbstractMessageCommand {

    protected SimpleMessageCommand(MessagePublisher publisher) {
        this(null, null, ExecutionStrategy.sync(), publisher);
    }

    protected SimpleMessageCommand(String key, String description, MessagePublisher publisher) {
        this(key, description, ExecutionStrategy.sync(), publisher);
    }

    protected SimpleMessageCommand(String key, String description,
                                   ExecutionStrategy executionStrategy,
                                   MessagePublisher publisher) {
        super(key, description, executionStrategy, publisher);
    }
}