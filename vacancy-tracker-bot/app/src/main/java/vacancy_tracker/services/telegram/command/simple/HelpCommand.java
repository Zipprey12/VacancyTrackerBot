package vacancy_tracker.services.telegram.command.simple;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.view.Describable;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingMessagePublisher;

import java.util.List;

@Component
public class HelpCommand extends CompletableMessageCommand {

    private static final String MESSAGE_HEADER = """
            *Список доступных команд*:
            
            """;

    private String message;

    public HelpCommand(SendingMessagePublisher publisher,
                       List<? extends Describable> commands) {
        super("/help", "Вывод справочной информации", publisher);
        initMessage(commands);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText(message);
    }

    private void initMessage(List<? extends Describable> commands) {
        StringBuilder builder = new StringBuilder();
        builder.append(MESSAGE_HEADER);
        for (var command : commands) {
            if (command.getKey() == null) {
                continue;
            }
            builder.append(prepareKey(command.getKey()))
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        this.message = builder.toString();
    }

    private String prepareKey(String key) {
        return key.replace("_", "\\_");
    }
}
