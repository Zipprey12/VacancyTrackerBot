package vacancy_tracker.repository;

import vacancy_tracker.model.search.VacancySearchFilter;

import java.util.Optional;

public interface SearchFiltersRepository {

    VacancySearchFilter save(long sessionId, VacancySearchFilter filter);

    Optional<VacancySearchFilter> get(long sessionId);

    void remove(long sessionId);
}
