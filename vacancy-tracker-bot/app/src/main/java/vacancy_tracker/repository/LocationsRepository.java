package vacancy_tracker.repository;

import vacancy_tracker.model.domain.Region;
import vacancy_tracker.model.domain.Town;

import java.util.List;
import java.util.Optional;

public interface LocationsRepository {

    void insertTowns(List<Town> towns);

    void insertRegions(List<Region> regions);

    Optional<Region> getRegionByTownId(int townId);

    Optional<Region> getRegionByCode(int regionId);

    Optional<Region> getRegionBasicByCode(int regionId);

    Optional<Town> getTownById(int townId);

    List<Region> getAllRegionsBasic();
}
