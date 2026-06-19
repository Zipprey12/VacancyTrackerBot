package vacancy_tracker.services.telegram.actions.message;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.actions.MessageAction;
import vacancy_tracker.services.telegram.command.publishers.SendingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;

@Component
public class InitMessage extends MessageAction {

    public static final String MESSAGE = """
            👋 *Привет! Я бот для поиска вакансий!*
            
            🔍 Помогаю найти работу на SuperJob и TrudVsem
            """;

    public InitMessage(SendingMessagePublisher publisher) {
        super(ExecutionStrategy.sync(), publisher);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText(MESSAGE);
    }
}