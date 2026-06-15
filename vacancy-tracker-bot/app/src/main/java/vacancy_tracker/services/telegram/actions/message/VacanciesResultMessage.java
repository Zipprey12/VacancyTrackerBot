package vacancy_tracker.services.telegram.actions.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;
import vacancy_tracker.services.telegram.view.formatters.vacancies.PageableVacanciesFormatter;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacancyPageFormatter;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacancySourcesFormatter;

import static vacancy_tracker.services.telegram.view.formatters.vacancies.VacanciesMessageFormatter.MAX_VACANCIES;

@Component
@RequiredArgsConstructor
public class VacanciesResultMessage {

    private final PageableVacanciesFormatter pageableVacanciesFormatter;
    private final VacancySourcesFormatter vacancySourcesFormatter;
    private final VacancyPageFormatter vacancyPageFormatter;

    public void fillMessage(VacanciesResponse response, OutgoingMessage message, VacanciesShownParams shownParams) {
        if (response.getTotal() <= MAX_VACANCIES && !shownParams.isHasAnother()) {
            vacancyPageFormatter.fillMessage(message, response);
            return;
        }
        pageableVacanciesFormatter.fill(message, response, shownParams);
    }

    public void fillMessage(SearchResult result, OutgoingMessage message) {
        if (result.getTotalCount() <= MAX_VACANCIES) {
            vacancyPageFormatter.fillMessage(message, result);
        } else if (result.getNotEmptyResponseCount() == 1) {
            var params = new VacanciesShownParams(true, false);
            pageableVacanciesFormatter.fill(message, result.getNotEmptyResponses().getFirst(), params);
        } else {
            vacancySourcesFormatter.fill(message, result.getNotEmptySources(), result.getTotalCount(), result.getModifiedFrom());
        }
    }
}
