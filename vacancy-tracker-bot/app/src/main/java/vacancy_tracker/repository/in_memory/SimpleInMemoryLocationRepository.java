package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.repository.LocationsRepository;

import java.util.*;

@Repository
public class SimpleInMemoryLocationRepository implements LocationsRepository {

    private final Map<Integer, Region> byCityId = new LinkedHashMap<>();
    private final Map<Integer, Region> byRegionId = new LinkedHashMap<>();
    private final Map<Integer, Town> townsById = new LinkedHashMap<>();

    @Override
    public void insertTowns(List<Town> towns) {
        towns.forEach(t -> {
            townsById.put(t.getId(), t);

            var regionId = t.getRegionId();
            var region = byRegionId.get(regionId);
            if (region != null) {
                region.addCity(t);
                byCityId.put(t.getId(), region);
            }
        });
    }

    @Override
    public void insertRegions(List<Region> regions) {
        regions.forEach(r -> byRegionId.put(r.getId(), r));
    }

    @Override
    public Optional<Region> getRegionByTownId(int townId) {
        return Optional.ofNullable(byCityId.get(townId));
    }

    @Override
    public Optional<Region> getRegionById(int regionId) {
        return Optional.ofNullable(byRegionId.get(regionId));
    }

    @Override
    public Optional<Region> getRegionBasicById(int regionId) {
        return getRegionById(regionId);
    }

    @Override
    public Optional<Town> getTownById(int townId) {
        return Optional.ofNullable(townsById.get(townId));
    }

    @Override
    public boolean isInitialized() {
        return !byRegionId.isEmpty();
    }
}
