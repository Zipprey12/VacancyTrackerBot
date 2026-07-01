package vacancy_tracker.services.telegram.actions.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;
import vacancy_tracker.services.telegram.view.formatters.vacancies.PageableVacanciesFormatter;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacanciesPageFormatter;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacancySourcesFormatter;

import static vacancy_tracker.services.telegram.view.formatters.vacancies.VacanciesMessageFormatter.MAX_VACANCIES;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacanciesResultMessage {

    private final PageableVacanciesFormatter pageableVacanciesFormatter;
    private final VacancySourcesFormatter vacancySourcesFormatter;
    private final VacanciesPageFormatter vacancyPageFormatter;

    public void fillMessage(VacanciesResponse response, OutgoingMessage message, VacanciesShownParams shownParams) {
        if (!response.isCanHasOther() && !shownParams.isHasAnother()) {
            vacancyPageFormatter.fillMessage(message, response);
            return;
        }
        pageableVacanciesFormatter.fill(message, response, shownParams);
    }

    public void fillMessage(SearchResult result, OutgoingMessage message) {
        if (result.getTotalCount() <= MAX_VACANCIES && !result.getCanHasAnother()) {
            vacancyPageFormatter.fillMessage(message, result);
        } else if (result.getNotEmptyResponseCount() == 1) {
            var params = new VacanciesShownParams(true, false);
            pageableVacanciesFormatter.fill(message, result.getNotEmptyResponses().getFirst(), params);
        } else {
            vacancySourcesFormatter.fill(message, result);
        }
    }

    public void fillSourceMessage(SearchResult result, OutgoingMessage message) {
        vacancySourcesFormatter.fill(message, result);
    }
}
