package vacancy_tracker.services.telegram.command;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.message.MessageSender;

public class InitCommand extends SendingMessageCommand {

    public InitCommand(MessageSender sender) {
        super("/init", "Сообщение при первом запуске", sender);
    }

    @Override
    public void execute(Message message) {
        sender.sendText(message.getChatId(), "Хэллоу брача впервые!");
    }
}
