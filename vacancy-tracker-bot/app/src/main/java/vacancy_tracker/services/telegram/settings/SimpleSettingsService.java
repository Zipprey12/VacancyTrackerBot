package vacancy_tracker.services.telegram.settings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.repository.SettingsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleSettingsService implements SettingsService {

    private final SettingsRepository repository;

    @Override
    public void saveFilters(long sessionId, VacancySearchFilter filter) {
        repository.saveFilters(sessionId, filter);
        log.info("Сохранены изменения сессии");
    }

    @Override
    public VacancySearchFilter getFilters(long sessionId) {
        var existed = repository.getFilters(sessionId);

        if (existed.isPresent()) {
            return existed.get();
        }

        var filter = VacancySearchFilter.builder()
                .limit(10)
                .build();

        repository.saveFilters(sessionId, filter);
        return filter;
    }

    @Override
    public void deleteFilters(long sessionId) {
        repository.removeFilters(sessionId);
    }
}
