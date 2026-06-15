package vacancy_tracker.services.telegram.command.vacancies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.actions.vacancies.SendVacanciesAction;
import vacancy_tracker.services.telegram.command.SimpleMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;

@Slf4j
@Component
public class SendAllVacanciesCommand extends SimpleMessageCommand {

    private final SendVacanciesAction sendVacanciesAction;

    protected SendAllVacanciesCommand(SendingAndUpdatingMessagePublisher publisher,
                                      SendVacanciesAction sendVacanciesAction) {

        super("/search", "Поиск всех подходящих вакансий", publisher);
        this.sendVacanciesAction = sendVacanciesAction;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        sendVacanciesAction.execute(messageData);
        messageData.setText(null);
    }
}
