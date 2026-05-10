package vacancy_tracker.repository;

import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.api.entity.Town;

import java.util.List;
import java.util.Optional;

public interface LocationsRepository {

    void insertTowns(List<Town> towns);

    void insertRegions(List<Region> regions);

    Optional<Region> getRegionByTownId(int townId);

    Optional<Region> getRegionById(int regionId);

    Optional<Region> getRegionBasicById(int regionId);

    Optional<Town> getTownById(int townId);

    List<Region> getAllRegionsBasic();

    boolean isInitialized();
}
