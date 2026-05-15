package vacancy_tracker.services.telegram.command.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Vacancy;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.MessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.formatters.VacanciesMessageFormatter;
import vacancy_tracker.services.vacancy.VacancyService;
import vacancy_tracker.sources.superjob.service.vacancy.SuperJobVacanciesService;

import java.util.List;

@Component
@Slf4j
public class ForceSearchVacanciesCommand extends MessageCommand {

    //todo Тут должен быть общий сервис, который будет искать вакансии во всех api
    private final VacancyService vacanciesService;
    private final SettingsService settingsService;
    private final VacanciesMessageFormatter messageFormatter;

    public ForceSearchVacanciesCommand(SendingAndUpdatingMessagePublisher publisher,
                                       SettingsService settingsService,
                                       SuperJobVacanciesService vacanciesService,
                                       VacanciesMessageFormatter messageFormatter) {

        super("/get_now", "Вывод всего и сразу", publisher);
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
