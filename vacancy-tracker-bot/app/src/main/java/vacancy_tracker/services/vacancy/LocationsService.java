package vacancy_tracker.services.vacancy;

import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.model.api.entity.Region;

import java.util.List;
import java.util.Optional;

public interface LocationsService {

    void initialize();

    Optional<Region> getRegionByTownId(int townId);

    Optional<Region> getRegionById(int regionId);

    Optional<Location> getLocationByRegionId(int regionId);

    Optional<Location> getLocationByTownId(int townId);

    List<Region> getAllRegionsBasic();

    boolean isInitialized();
}
