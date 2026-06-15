package vacancy_tracker.services.api.location;

import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.domain.Region;

import java.util.List;
import java.util.Optional;

public interface LocationsService {

    void initialize();

    Optional<Region> getRegionByCode(int code);

    Optional<Location> getLocationByRegionCode(int code);

    Optional<Location> getLocationByTownId(int townId);

    List<Region> getAllRegionsBasic();
}
