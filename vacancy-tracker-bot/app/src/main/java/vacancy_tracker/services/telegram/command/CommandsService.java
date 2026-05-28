package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommandsService {

    @Getter(AccessLevel.PROTECTED)
    private final ConcurrentHashMap<String, MessageCommand> shownCommands;

    private final ConcurrentHashMap<String, InputInterceptingCommand<?>> inputInterceptingCommands;

    public CommandsService() {
        this.inputInterceptingCommands = new ConcurrentHashMap<>();
        this.shownCommands = new ConcurrentHashMap<>();
    }

    @Autowired
    public void setShownCommands(List<MessageCommand> commands) {
        commands.stream()
                .filter(c -> c.getDescription() != null)
                .forEach(c -> this.shownCommands.put(c.getKey(), c));
    }

    @Autowired
    public void setInputInterceptingCommands(List<InputInterceptingCommand<?>> inputInterceptingCommands) {
        inputInterceptingCommands.stream()
                .filter(c -> c.getKey() != null)
                .forEach(c -> this.inputInterceptingCommands.put(c.getKey(), c));
    }

    public Optional<MessageCommand> getShown(String commandText) {
        var key = getKey(commandText);
        var command = shownCommands.get(key);
        if (command == null) {
            return Optional.empty();
        }
        return Optional.of(command);
    }

    @SuppressWarnings("java:S1452")
    public Optional<InputInterceptor<?>> getInterceptorByCommandKey(String key) {
        var command = inputInterceptingCommands.get(key);
        if (command != null) {
            return Optional.ofNullable(command.getInputInterceptor());
        }
        return Optional.empty();
    }

    protected String getKey(String input) {
        input = input.strip().toLowerCase();

        var end = input.indexOf(" ");
        return end == -1 ? input : input.substring(0, end);
    }
}