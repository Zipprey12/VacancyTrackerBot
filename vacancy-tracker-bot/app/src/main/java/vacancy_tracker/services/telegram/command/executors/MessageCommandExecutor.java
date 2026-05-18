package vacancy_tracker.services.telegram.command.executors;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageCommandExecutor {

    @Getter(AccessLevel.PROTECTED)
    private final ConcurrentHashMap<String, CompletableMessageCommand> commands;

    protected MessageCommandExecutor(@Qualifier("allCommands") List<CompletableMessageCommand> commands) {
        this.commands = new ConcurrentHashMap<>();
        commands.forEach(c -> this.commands.putIfAbsent(c.getKey(), c));
    }

    public boolean execute(MessageData message) {
        var key = getKey(message.getText());
        var command = commands.get(key);

        if (command == null) {
            return false;
        }

        command.execute(message);
        return true;
    }

    protected String getKey(String input) {
        input = input.strip().toLowerCase();

        var end = input.indexOf(" ");
        return end == -1 ? input : input.substring(0, end);
    }
}
