package vacancy_tracker.services.telegram.command;

import lombok.RequiredArgsConstructor;
import vacancy_tracker.services.telegram.message.MessageSender;

@RequiredArgsConstructor
public abstract class SimpleMessageCommand implements MessageBotCommand {

    protected final MessageSender sender;

}
