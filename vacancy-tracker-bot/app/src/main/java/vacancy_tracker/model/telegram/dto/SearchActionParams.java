package vacancy_tracker.model.telegram.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.model.search.VacanciesSearchRequest;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;

@Data
@RequiredArgsConstructor
public class SearchActionParams {

    private final VacanciesSearchRequest searchParams;
    private final VacanciesShownParams shownParams;

    private boolean publish = true;
}
