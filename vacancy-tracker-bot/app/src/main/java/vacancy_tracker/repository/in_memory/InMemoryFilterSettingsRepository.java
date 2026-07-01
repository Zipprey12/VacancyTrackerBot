package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.repository.SearchFiltersRepository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class InMemoryFilterSettingsRepository implements SearchFiltersRepository {

    private final HashMap<Long, VacancySearchFilter> filters = new HashMap<>();

    @Override
    public VacancySearchFilter save(long sessionId, VacancySearchFilter filter) {
        this.filters.put(sessionId, filter);
        return filter;
    }

    @Override
    public Optional<VacancySearchFilter> get(long sessionId) {
        return Optional.ofNullable(filters.get(sessionId));
    }

    @Override
    public void remove(long sessionId) {
        filters.remove(sessionId);
    }
}
