package vacancy_tracker.services.telegram.command.simple;

import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.SendingAndUpdatingMessageCommand;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;

public class InitCommand extends SendingAndUpdatingMessageCommand {

    public InitCommand(MessageSender sender, MessageEditor editor) {
        super("/init", "Сообщение при первом запуске", sender, editor);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText("Привет! Я бот для поиска вакансий!");
    }
}
