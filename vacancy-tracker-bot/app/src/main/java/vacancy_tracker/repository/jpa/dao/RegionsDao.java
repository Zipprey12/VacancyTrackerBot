package vacancy_tracker.repository.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vacancy_tracker.model.api.entity.RegionEntity;
import vacancy_tracker.repository.RegionsRepository;

public interface RegionsDao extends RegionsRepository, JpaRepository<RegionEntity, Integer> {
}
