package vacancy_tracker.services.telegram.settings;

import vacancy_tracker.model.api.dto.VacancySearchFilter;

public interface SettingsService {

    void saveFilters(long sessionId, VacancySearchFilter filter);

    VacancySearchFilter getFilters(long sessionId);
}
