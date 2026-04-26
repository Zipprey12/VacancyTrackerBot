package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.services.telegram.message.MessageSender;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class SendingMessageCommand implements MessageBotCommand {

    private final String key;

    private final String description;

    protected final MessageSender sender;

    @Override
    public String getKey(){
        return key;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
