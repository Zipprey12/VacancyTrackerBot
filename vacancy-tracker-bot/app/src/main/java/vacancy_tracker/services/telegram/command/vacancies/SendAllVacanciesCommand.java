package vacancy_tracker.services.telegram.command.vacancies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.RequestType;
import vacancy_tracker.model.api.dto.SearchResult;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.dto.VacanciesSearchParams;
import vacancy_tracker.services.api.VacanciesSearcher;
import vacancy_tracker.services.telegram.command.ExtendedMessageCommand;
import vacancy_tracker.services.telegram.command.execution.strategy.AsyncExecutionStrategy;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.vacancies.PageableVacanciesFormatter;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacancyPageFormatter;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacancySourcesFormatter;

import java.time.LocalDateTime;

import static vacancy_tracker.services.telegram.view.formatters.vacancies.VacancyPageFormatter.MAX_VACANCIES;

@Slf4j
@Component
public class SendAllVacanciesCommand extends ExtendedMessageCommand<VacanciesSearchParams> {

    private final VacanciesSearcher vacanciesSearcher;
    private final SearchFiltersService settingsService;
    private final PageableVacanciesFormatter pageableVacanciesFormatter;
    private final VacancyPageFormatter vacancyPageFormatter;
    private final VacancySourcesFormatter vacancySourcesFormatter;

    protected SendAllVacanciesCommand(SendingAndUpdatingMessagePublisher publisher,
                                      VacanciesSearcher vacanciesSearcher,
                                      SearchFiltersService settingsService,
                                      PageableVacanciesFormatter pageableVacanciesFormatter,
                                      VacancyPageFormatter messageFormatter,
                                      AsyncExecutionStrategy executionStrategy,
                                      VacancySourcesFormatter vacancySourcesFormatter) {

        super("/get_all", "Получить все вакансии по запросу", publisher, executionStrategy, null);
        this.vacanciesSearcher = vacanciesSearcher;
        this.settingsService = settingsService;
        this.pageableVacanciesFormatter = pageableVacanciesFormatter;
        this.vacancyPageFormatter = messageFormatter;
        this.vacancySourcesFormatter = vacancySourcesFormatter;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage message) {
        long id = message.getChatId();
        var filter = settingsService.get(id);
        filter.setRequestType(RequestType.MANUAL);
        var result = vacanciesSearcher.search(filter, 10, 0).join();
        handleResult(result, message, null, true);
    }

    @Override
    protected void executeWithParameters(MessageData messageData, VacanciesSearchParams parameter) {
        var message = new OutgoingMessage(messageData);
        var filter = settingsService.get(messageData.getChatId());
        if (parameter.getStartDate() != null) {
            filter.setModifiedFrom(parameter.getStartDate());
        }
        filter.setRequestType(parameter.getRequestType());
        var source = parameter.getSource();
        log.info("Source: {}", source);
        var result = vacanciesSearcher.search(filter, 10, parameter.getPage(), source).join();
        handleResult(result, message, parameter.getStartDate(), true);
        getPublisher().publish(message);
    }

    private void handleResult(SearchResult result, OutgoingMessage messageData, LocalDateTime from, boolean showBack) {
        result.setModifiedFrom(from);
        if (result.getTotalCount() <= MAX_VACANCIES) {
            vacancyPageFormatter.fillMessage(messageData, result);
        } else if (result.getNotEmptyResponseCount() == 1) {
            pageableVacanciesFormatter.fill(messageData, result.getNotEmptyResponses().getFirst(), result.getRequestType(), showBack);
        } else {
            vacancySourcesFormatter.fill(messageData, result.getNotEmptySources(), result.getTotalCount(), from);
        }
    }
}
