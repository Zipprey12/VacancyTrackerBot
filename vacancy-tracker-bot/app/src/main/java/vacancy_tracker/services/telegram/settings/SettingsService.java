package vacancy_tracker.services.telegram.settings;

import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.dto.VacancySearchFilter;

@Service
public interface SettingsService {

    void saveFilters(long sessionId, VacancySearchFilter filter);

    VacancySearchFilter getFilters(long sessionId);
}
