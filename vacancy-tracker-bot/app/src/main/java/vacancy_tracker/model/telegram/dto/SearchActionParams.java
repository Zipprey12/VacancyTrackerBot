package vacancy_tracker.model.telegram.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.model.search.VacanciesSearchParams;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;

@Data
@RequiredArgsConstructor
public class SearchActionParams {

    private final VacanciesSearchParams searchParams;
    private final VacanciesShownParams shownParams;

    private boolean publish = true;
}
