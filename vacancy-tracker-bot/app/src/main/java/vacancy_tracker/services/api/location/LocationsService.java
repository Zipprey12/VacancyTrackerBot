package vacancy_tracker.services.api.location;

import vacancy_tracker.model.api.ExtendedRegion;
import vacancy_tracker.model.api.Location;

import java.util.List;
import java.util.Optional;

public interface LocationsService {

    void initialize();

    Optional<ExtendedRegion> getRegionByCode(int code);

    Optional<Location> getLocationByRegionCode(int code);

    Optional<Location> getLocationByTownId(int townId);

    List<ExtendedRegion> getAllRegionsBasic();
}
