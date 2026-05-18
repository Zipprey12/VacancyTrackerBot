package vacancy_tracker.services.telegram.settings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.repository.SearchFiltersRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleSearchFiltersService implements SearchFiltersService {

    private final SearchFiltersRepository repository;

    @Override
    public void save(long sessionId, VacancySearchFilter filter) {
        repository.save(sessionId, filter);
        log.info("Сохранены изменения сессии");
    }

    @Override
    public VacancySearchFilter get(long sessionId) {
        var existed = repository.get(sessionId);

        if (existed.isPresent()) {
            return existed.get();
        }

        var filter = VacancySearchFilter.builder()
                .limit(10)
                .build();

        repository.save(sessionId, filter);
        return filter;
    }

    @Override
    public void delete(long sessionId) {
        repository.remove(sessionId);
    }
}
