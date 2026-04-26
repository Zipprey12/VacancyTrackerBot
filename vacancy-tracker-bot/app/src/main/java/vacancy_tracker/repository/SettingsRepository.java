package vacancy_tracker.repository;

import vacancy_tracker.model.api.dto.VacancySearchFilter;

import java.util.Optional;

public interface SettingsRepository {

    void saveFilters(long sessionId, VacancySearchFilter filter);

    Optional<VacancySearchFilter> getFilters(long sessionId);
}
