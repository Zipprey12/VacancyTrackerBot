package vacancy_tracker.services.telegram.command.simple;

import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.MessageBotCommand;
import vacancy_tracker.services.telegram.command.SendingAndUpdatingMessageCommand;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;

import java.util.List;

public class HelpCommand extends SendingAndUpdatingMessageCommand {

    private static final String MESSAGE_HEADER = """
            *Список доступных команд*:
            
            """;

    private String message;

    public HelpCommand(MessageSender sender,
                       MessageEditor editor,
                       List<MessageBotCommand> commands) {
        super("/help", "Вывод справочной информации", sender, editor);
        initMessage(commands);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText(message);
    }

    private void initMessage(List<MessageBotCommand> commands) {
        StringBuilder builder = new StringBuilder();
        builder.append(MESSAGE_HEADER);
        for (var command : commands) {
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
