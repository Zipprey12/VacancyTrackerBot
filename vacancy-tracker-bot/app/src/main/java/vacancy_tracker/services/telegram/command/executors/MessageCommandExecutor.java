package vacancy_tracker.services.telegram.command.executors;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.MessageBotCommand;

import java.util.HashMap;
import java.util.List;

@Service
public class MessageCommandExecutor {

    @Getter(AccessLevel.PROTECTED)
    private final HashMap<String, MessageBotCommand> commands;

    protected MessageCommandExecutor(@Qualifier("allCommands") List<MessageBotCommand> commands) {
        this.commands = new HashMap<>();
        commands.forEach(c -> this.commands.putIfAbsent(c.getKey(), c));
    }

    public boolean execute(Message message) {
        var input = message.getText();
        var key = getKey(input);

        if (!commands.containsKey(key)) {
            return false;
        }

        var command = commands.get(key);
        command.execute(message);
        return true;
    }

    protected String getKey(String input) {
        input = input.strip().toLowerCase();

        var end = input.indexOf(" ");
        return end == -1 ? input : input.substring(0, end);
    }
}
