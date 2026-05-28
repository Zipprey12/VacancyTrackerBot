package vacancy_tracker.services.telegram.command.execution.strategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;

import java.util.function.Consumer;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class CommandExecutor {

    private final Consumer<OutgoingMessage> populator;
    private final MessagePublisher publisher;

    public abstract void execute(MessageData messageData);
}
