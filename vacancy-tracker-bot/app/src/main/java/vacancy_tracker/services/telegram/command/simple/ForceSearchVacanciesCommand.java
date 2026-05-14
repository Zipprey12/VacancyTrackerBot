package vacancy_tracker.services.telegram.command.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Vacancy;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.SendingAndUpdatingMessageCommand;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.VacanciesMessageFormatter;
import vacancy_tracker.services.vacancy.VacancyService;

import java.util.List;

@Slf4j
public class ForceSearchVacanciesCommand extends SendingAndUpdatingMessageCommand {

    //todo Тут должен быть общий сервис, который будет искать вакансии во всех api
    private final VacancyService vacanciesService;
    private final SettingsService settingsService;
    private final VacanciesMessageFormatter messageFormatter;

    public ForceSearchVacanciesCommand(MessageSender sender,
                                       MessageEditor editor,
                                       SettingsService settingsService,
                                       VacancyService vacanciesService,
                                       VacanciesMessageFormatter messageFormatter) {

        super("/get_now", "Вывод всего и сразу", sender, editor);
        this.settingsService = settingsService;
        this.vacanciesService = vacanciesService;
        this.messageFormatter = messageFormatter;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        long id = messageData.getChatId();
        var filter = settingsService.getFilters(id);

        try {
            List<Vacancy> vacancies = vacanciesService.search(filter).join();
            messageData.setText(generateText(vacancies));
        } catch (Exception e) {
            fillError(e, messageData);
        }
    }

    private String generateText(List<Vacancy> vacancies) {
        return messageFormatter.format(vacancies);
    }

    private void fillError(Throwable throwable, OutgoingMessage messageData) {
        log.error("Ошибка поиска вакансий: {}", throwable.getMessage());
        messageData.setText("❌ Произошла ошибка при поиске вакансий. Попробуйте позже.");
    }
}
