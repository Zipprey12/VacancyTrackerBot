package vacancy_tracker.repository;

import vacancy_tracker.model.api.entity.RegionEntity;

import java.util.List;

public interface RegionsRepository {

    List<RegionEntity> findAll();
}
