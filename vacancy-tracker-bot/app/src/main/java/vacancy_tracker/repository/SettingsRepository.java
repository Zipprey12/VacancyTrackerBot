package vacancy_tracker.repository;

import vacancy_tracker.model.vacancy.dto.VacancySearchFilterDto;

import java.util.Optional;

public interface SettingsRepository {

    void saveFilters(long sessionId, VacancySearchFilterDto filter);

    Optional<VacancySearchFilterDto> getFilters(long sessionId);
}
