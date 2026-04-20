package vacancy_tracker.services.telegram.command;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.message.MessageSender;

public class InitCommand extends SimpleMessageCommand {

    public InitCommand(MessageSender sender) {
        super(sender);
    }

    @Override
    public String getKey() {
        return "/init";
    }

    @Override
    public String getDescription() {
        return "Сообщение при первом запуске";
    }

    @Override
    public void execute(Message message) {
        sender.sendText(message.getChatId(), "Хэллоу брача впервые!");
    }
}
