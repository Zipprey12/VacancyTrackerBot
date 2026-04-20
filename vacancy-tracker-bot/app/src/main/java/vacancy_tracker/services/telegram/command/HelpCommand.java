package vacancy_tracker.services.telegram.command;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.message.MessageSender;

import java.util.List;

public class HelpCommand extends SimpleMessageCommand {

    private static final String MESSAGE_HEADER = """
            Список доступных команд:
            """;

    private String message;

    public HelpCommand(MessageSender sender, List<MessageBotCommand> commands) {
        super(sender);
        initMessage(commands);
    }

    @Override
    public void execute(Message message) {
        var id = message.getChatId();
        sender.sendText(id, this.message);
    }

    @Override
    public String getKey() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Вывод справочной информации";
    }

    private void initMessage(List<MessageBotCommand> commands) {
        StringBuilder builder = new StringBuilder();
        builder.append(MESSAGE_HEADER);
        for (var command : commands) {
            builder.append(command.getKey())
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        this.message = builder.toString();
    }
}
