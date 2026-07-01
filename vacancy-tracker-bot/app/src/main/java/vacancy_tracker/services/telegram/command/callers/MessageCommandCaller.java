package vacancy_tracker.services.telegram.command.callers;

import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.CommandsService;

import java.util.concurrent.CompletableFuture;

@Service
public class MessageCommandCaller {

    private final CommandsService commandsService;

    protected MessageCommandCaller(CommandsService commandsService) {
        this.commandsService = commandsService;
    }

    public CompletableFuture<Boolean> execute(MessageData message) {
        var key = message.getText();
        var command = commandsService.getShown(key);

        if (command.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        command.get().execute(message);
        return CompletableFuture.completedFuture(true);
    }
}
