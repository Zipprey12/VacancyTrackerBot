package vacancy_tracker.services.telegram.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.repository.in_memory.SimpleInMemorySettingsRepository;

@Service
@RequiredArgsConstructor
public class SimpleSettingsService implements SettingsService {

    private final SimpleInMemorySettingsRepository repository;

    @Override
    public void saveFilters(long sessionId, VacancySearchFilter filter) {
        repository.saveFilters(sessionId, filter);
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
}
