package vacancy_tracker.services.telegram.command.simple;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingMessagePublisher;

@Component
public class InitCommand extends CompletableMessageCommand {

    public InitCommand(SendingMessagePublisher publisher) {
        super("/init", "Сообщение при первом запуске", publisher);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText("Привет! Я бот для поиска вакансий!");
    }
}