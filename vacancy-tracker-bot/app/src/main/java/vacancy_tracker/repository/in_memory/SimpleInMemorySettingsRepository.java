package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.vacancy.dto.VacancySearchFilterDto;
import vacancy_tracker.repository.SettingsRepository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class SimpleInMemorySettingsRepository implements SettingsRepository {

    private final HashMap<Long, VacancySearchFilterDto> filters = new HashMap<>();

    @Override
    public void saveFilters(long sessionId, VacancySearchFilterDto filter) {
        filters.putIfAbsent(sessionId, filter);
    }

    @Override
    public Optional<VacancySearchFilterDto> getFilters(long sessionId) {
        return Optional.ofNullable(filters.get(sessionId));
    }
}
