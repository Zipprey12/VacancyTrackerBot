package vacancy_tracker.services.telegram.actions.vacancies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.RequestType;
import vacancy_tracker.model.api.dto.SearchResult;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.dto.VacanciesSearchParams;
import vacancy_tracker.services.api.VacanciesSearcher;
import vacancy_tracker.services.telegram.actions.ExtendedMessageAction;
import vacancy_tracker.services.telegram.command.execution.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.vacancies.PageableVacanciesFormatter;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacancyPageFormatter;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacancySourcesFormatter;

import java.time.LocalDateTime;

import static vacancy_tracker.services.telegram.view.formatters.vacancies.VacanciesMessageFormatter.MAX_VACANCIES;

@Slf4j
@Component
public class ShowVacanciesAction extends ExtendedMessageAction<VacanciesSearchParams> {

    private final VacanciesSearcher vacanciesSearcher;
    private final SearchFiltersService settingsService;
    private final PageableVacanciesFormatter pageableVacanciesFormatter;
    private final VacancySourcesFormatter vacancySourcesFormatter;
    private final VacancyPageFormatter vacancyPageFormatter;

    public ShowVacanciesAction(ExecutionStrategy asyncStrategy,
                               SendingAndUpdatingMessagePublisher publisher,
                               VacanciesSearcher vacanciesSearcher,
                               SearchFiltersService settingsService,
                               PageableVacanciesFormatter messageFormatter,
                               VacancySourcesFormatter vacancySourcesFormatter,
                               VacancyPageFormatter vacancyPageFormatter) {

        super(asyncStrategy, publisher);
        this.vacanciesSearcher = vacanciesSearcher;
        this.settingsService = settingsService;
        this.pageableVacanciesFormatter = messageFormatter;
        this.vacancySourcesFormatter = vacancySourcesFormatter;
        this.vacancyPageFormatter = vacancyPageFormatter;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage outgoingMessage) {
        long id = outgoingMessage.getChatId();
        var filter = settingsService.get(id);
        filter.setRequestType(RequestType.MANUAL);
        vacanciesSearcher.search(filter, 1, 0)
                .thenAccept(r -> handleResult(r, outgoingMessage, null, true));
    }

    @Override
    protected void executeWithParameters(OutgoingMessage messageData, VacanciesSearchParams parameter) {
        var filter = settingsService.get(messageData.getChatId());
        if (parameter.getStartDate() != null) {
            filter.setModifiedFrom(parameter.getStartDate());
        }
        filter.setRequestType(parameter.getRequestType());
        var source = parameter.getSource();
        log.info("Source: {}", source);
        vacanciesSearcher.search(filter, 10, parameter.getPage(), source)
                .thenAccept(r -> handleResult(r, messageData, parameter.getStartDate(), true));
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
        getPublisher().publish(messageData);
    }
}
