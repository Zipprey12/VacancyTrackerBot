package vacancy_tracker.services.telegram.command;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.message.MessageSender;

import java.util.List;

public class HelpCommand extends SendingMessageCommand {

    private static final String MESSAGE_HEADER = """
            *Список доступных команд*:
            
            """;

    private String message;

    public HelpCommand(MessageSender sender,
                       List<MessageBotCommand> commands) {
        super("/help", "Вывод справочной информации", sender);
        initMessage(commands);
    }

    @Override
    public void execute(Message message) {
        var id = message.getChatId();
        sender.sendText(id, this.message, ParseMode.MARKDOWN);
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
