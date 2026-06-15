package vacancy_tracker.services.telegram.settings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.repository.SearchFiltersRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchFiltersServiceImpl implements SearchFiltersService {

    private final SearchFiltersRepository repository;

    @Override
    @CachePut(value = "filters", key = "#sessionId")
    public VacancySearchFilter save(long sessionId, VacancySearchFilter filter) {
        repository.save(sessionId, filter);
        log.info("Сохранены изменения сессии");
        return filter;
    }

    @Override
    @Cacheable(value = "filters", key = "#sessionId")
    public VacancySearchFilter get(long sessionId) {
        var existed = repository.get(sessionId);
        if (existed.isPresent()) {
            return existed.get();
        }

        var filter = new VacancySearchFilter();
        repository.save(sessionId, filter);
        return filter;
    }

    @Override
    @CacheEvict(value = "filters", key = "#sessionId")
    public void delete(long sessionId) {
        repository.remove(sessionId);
    }
}
