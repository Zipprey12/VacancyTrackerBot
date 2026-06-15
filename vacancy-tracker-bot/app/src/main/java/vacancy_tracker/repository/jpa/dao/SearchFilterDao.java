package vacancy_tracker.repository.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vacancy_tracker.model.persistence.SearchFilterEntity;

public interface SearchFilterDao extends JpaRepository<SearchFilterEntity, Long> {
}
