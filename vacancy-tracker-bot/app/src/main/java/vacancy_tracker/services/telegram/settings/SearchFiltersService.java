package vacancy_tracker.services.telegram.settings;

import vacancy_tracker.model.search.VacancySearchFilter;

public interface SearchFiltersService {

    VacancySearchFilter get(long sessionId);

    VacancySearchFilter save(long sessionId, VacancySearchFilter filter);

    void delete(long sessionId);
}
