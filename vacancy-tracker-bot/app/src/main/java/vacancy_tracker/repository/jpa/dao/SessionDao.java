package vacancy_tracker.repository.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vacancy_tracker.model.persistence.SessionEntity;

public interface SessionDao extends JpaRepository<SessionEntity, Long> {
}