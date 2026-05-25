package vacancy_tracker.repository.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vacancy_tracker.model.telegram.entities.SearchFilterEntity;

public interface SearchFilterDao extends JpaRepository<SearchFilterEntity, Long> {
}
