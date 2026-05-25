package vacancy_tracker.services.telegram.command.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.api.entity.Vacancy;
import vacancy_tracker.model.telegram.CallingSource;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.VacanciesMessageFormatter;
import vacancy_tracker.services.vacancy.VacancyService;
import vacancy_tracker.sources.superjob.service.vacancy.SuperJobVacanciesService;

import java.util.List;

@Component
@Slf4j
public class ForceSearchVacanciesCommand extends CompletableMessageCommand {

    //todo Тут должен быть общий сервис, который будет искать вакансии во всех api
    private final VacancyService vacanciesService;
    private final SearchFiltersService settingsService;
    private final VacanciesMessageFormatter messageFormatter;

    public ForceSearchVacanciesCommand(SendingAndUpdatingMessagePublisher publisher,
                                       SearchFiltersService settingsService,
                                       SuperJobVacanciesService vacanciesService,
                                       VacanciesMessageFormatter messageFormatter) {

        super("/get_now", "Вывод всего и сразу", publisher);
        this.settingsService = settingsService;
        this.vacanciesService = vacanciesService;
        this.messageFormatter = messageFormatter;
    }

    public void executeWithFilter(long chatId, VacancySearchFilter filter) {
        var message = new OutgoingMessage();
        message.setParseMode(ParseMode.MARKDOWN);
        message.setChatId(chatId);
        message.setSource(CallingSource.CHAT);

        fillVacancies(message, filter);
        getPublisher().publish(message);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        long id = messageData.getChatId();
        var filter = settingsService.get(id);
        fillVacancies(messageData, filter);
    }

    private void fillVacancies(OutgoingMessage messageData, VacancySearchFilter filter) {
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
