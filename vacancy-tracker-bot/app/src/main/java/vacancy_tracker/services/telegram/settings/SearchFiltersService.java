package vacancy_tracker.services.telegram.settings;

import vacancy_tracker.model.api.dto.VacancySearchFilter;

public interface SearchFiltersService {

    VacancySearchFilter get(long sessionId);

    void save(long sessionId, VacancySearchFilter filter);

    void delete(long sessionId);
}
