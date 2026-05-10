package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.repository.SettingsRepository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class SimpleInMemorySettingsRepository implements SettingsRepository {

    private final HashMap<Long, VacancySearchFilter> filters = new HashMap<>();

    @Override
    public void saveFilters(long sessionId, VacancySearchFilter filter) {
        this.filters.put(sessionId, filter);
    }

    @Override
    public Optional<VacancySearchFilter> getFilters(long sessionId) {
        return Optional.ofNullable(filters.get(sessionId));
    }
}
