package vacancy_tracker.services.vacancy;

import vacancy_tracker.model.vacancy.entity.Region;

import java.util.Optional;

public interface LocationsService {

    void initialize();

    Optional<Region> getByCityId(int cityId);

    Optional<Region> getByRegionId(int regionId);

    boolean isInitialized();
}
