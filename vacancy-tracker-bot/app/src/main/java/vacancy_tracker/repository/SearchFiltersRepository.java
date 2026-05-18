package vacancy_tracker.repository;

import vacancy_tracker.model.api.dto.VacancySearchFilter;

import java.util.Optional;

public interface SearchFiltersRepository {

    void save(long sessionId, VacancySearchFilter filter);

    Optional<VacancySearchFilter> get(long sessionId);

    void remove(long sessionId);
}
