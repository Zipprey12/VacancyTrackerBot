package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.repository.SearchFiltersRepository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class InMemoryFilterSettingsRepository implements SearchFiltersRepository {

    private final HashMap<Long, VacancySearchFilter> filters = new HashMap<>();

    @Override
    public void save(long sessionId, VacancySearchFilter filter) {
        this.filters.put(sessionId, filter);
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
