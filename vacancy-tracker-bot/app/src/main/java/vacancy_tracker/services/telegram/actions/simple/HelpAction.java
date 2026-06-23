package vacancy_tracker.services.telegram.actions.simple;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.view.Identifiable;
import vacancy_tracker.services.telegram.actions.MessageAction;
import vacancy_tracker.services.telegram.command.AbstractMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class HelpAction extends MessageAction {

    private static final String MESSAGE_HEADER = """
            *Список доступных команд*:
            
            """;

    private String message;

    public HelpAction(SendingMessagePublisher publisher,
                      List<AbstractMessageCommand> commands) {
        super(ExecutionStrategy.sync(), publisher);
        initMessage(commands);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText(message);
    }

    private void initMessage(List<AbstractMessageCommand> commands) {
        StringBuilder builder = new StringBuilder();
        builder.append(MESSAGE_HEADER);
        var filtered = commands.stream()
                .filter(c -> c.getDescription() != null)
                .toList();
        var set = new HashSet<>(filtered);
        for (var category : CommandCategory.values()) {
            addCategory(builder, category, set);
        }
        this.message = builder.toString();
    }

    private void addCategory(StringBuilder sb, CommandCategory category, Set<AbstractMessageCommand> commands) {
        var found = commands.stream()
                .filter(c -> c.getCategory() == category)
                .sorted(Comparator.comparing(Identifiable::getKey))
                .toList();
        if (found.isEmpty()) {
            return;
        }

        sb.append("*").append(category.getText()).append("*\n");
        found.forEach(c -> {
            commands.remove(c);
            sb.append(prepareKey(c.getKey()))
                    .append(" - ")
                    .append(c.getDescription())
                    .append("\n");
        });
        sb.append("\n");
    }

    private String prepareKey(String key) {
        return key.replace("_", "\\_");
    }
}
