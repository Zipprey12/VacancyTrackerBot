package vacancy_tracker.repository.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import vacancy_tracker.model.persistence.SearchFilterEntity;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.repository.SearchFiltersRepository;
import vacancy_tracker.repository.jpa.dao.SearchFilterDao;
import vacancy_tracker.services.mappers.SearchFilterMapper;

import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaSearchFiltersRepository implements SearchFiltersRepository {

    private final SearchFilterDao searchFilterDao;
    private final SearchFilterMapper mapper;

    @Override
    public Optional<VacancySearchFilter> get(long chatId) {
        var found = searchFilterDao.findById(chatId);
        return found.map(mapper::toDto);
    }

    @Override
    public VacancySearchFilter save(long chatId, VacancySearchFilter filter) {
        var entity = new SearchFilterEntity();
        entity.setChatId(chatId);

        mapper.updateEntity(filter, entity);
        var result = searchFilterDao.save(entity);
        return mapper.toDto(result);
    }

    @Override
    public void remove(long chatId) {
        searchFilterDao.deleteById(chatId);
    }
}