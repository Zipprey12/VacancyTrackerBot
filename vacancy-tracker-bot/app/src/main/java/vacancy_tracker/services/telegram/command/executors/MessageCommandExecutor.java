package vacancy_tracker.services.telegram.command.executors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.CommandsService;

import java.util.concurrent.CompletableFuture;

@Service
public class MessageCommandExecutor {

    private final CommandsService commandsService;

    protected MessageCommandExecutor(CommandsService commandsService) {
        this.commandsService = commandsService;
    }

    @Async
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
