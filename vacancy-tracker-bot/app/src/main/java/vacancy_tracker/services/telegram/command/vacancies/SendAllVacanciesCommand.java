package vacancy_tracker.services.telegram.command.vacancies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.actions.vacancies.SendVacanciesAction;
import vacancy_tracker.services.telegram.command.AbstractMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;

@Slf4j
@Component
public class SendAllVacanciesCommand extends AbstractMessageCommand {

    public static final String KEY = "/search";
    public static final String DESCRIPTION = "Поиск всех подходящих вакансий";
    private final SendVacanciesAction sendVacanciesAction;

    protected SendAllVacanciesCommand(SendingAndUpdatingMessagePublisher publisher,
                                      SendVacanciesAction sendVacanciesAction) {

        super(new CommandArgs(KEY, DESCRIPTION, null), publisher);
        this.sendVacanciesAction = sendVacanciesAction;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        sendVacanciesAction.execute(messageData);
        messageData.setText(null);
    }
}
