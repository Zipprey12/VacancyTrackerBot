package vacancy_tracker.services.telegram.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.services.telegram.actions.MessageAction;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;

@Slf4j
@Getter
public abstract class AbstractMessageCommand extends MessageAction implements MessageCommand {

    private final CommandCategory category;
    private final String key;
    private final String description;

    protected AbstractMessageCommand(CommandArgs args,
                                     ExecutionStrategy executionStrategy,
                                     MessagePublisher publisher) {
        super(executionStrategy == null ? ExecutionStrategy.sync() : executionStrategy, publisher);
        this.key = args.getKey();
        this.description = args.getDescription();
        this.category = args.getCategory() == null ? CommandCategory.OTHER : args.getCategory();
    }

    protected AbstractMessageCommand(CommandArgs args, MessagePublisher publisher) {
        super(ExecutionStrategy.sync(), publisher);
        this.key = args.getKey();
        this.description = args.getDescription();
        this.category = args.getCategory() == null ? CommandCategory.OTHER : args.getCategory();
    }
}