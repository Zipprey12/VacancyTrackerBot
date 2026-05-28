package vacancy_tracker.repository;

import vacancy_tracker.model.api.ExtendedRegion;
import vacancy_tracker.model.api.Town;

import java.util.List;
import java.util.Optional;

public interface LocationsRepository {

    void insertTowns(List<Town> towns);

    void insertRegions(List<ExtendedRegion> regions);

    Optional<ExtendedRegion> getRegionByTownId(int townId);

    Optional<ExtendedRegion> getRegionByCode(int regionId);

    Optional<ExtendedRegion> getRegionBasicByCode(int regionId);

    Optional<Town> getTownById(int townId);

    List<ExtendedRegion> getAllRegionsBasic();

    boolean isInitialized();
}
