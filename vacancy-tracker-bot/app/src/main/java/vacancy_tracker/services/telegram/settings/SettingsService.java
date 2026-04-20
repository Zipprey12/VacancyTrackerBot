package vacancy_tracker.services.telegram.settings;

import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.UserSession;
import vacancy_tracker.model.vacancy.dto.VacancySearchFilterDto;

@Service
public interface SettingsService {

    void saveFilters(long sessionId, VacancySearchFilterDto filter);

    VacancySearchFilterDto getFilters(long sessionId);
}
