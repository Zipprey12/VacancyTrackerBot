package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.vacancy.entity.City;
import vacancy_tracker.model.vacancy.entity.Region;
import vacancy_tracker.repository.LocationsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SimpleInMemoryLocationRepository implements LocationsRepository {

    private final Map<Integer, Region> byCityId = new HashMap<>();
    private final Map<Integer, Region> byRegionId = new HashMap<>();

    @Override
    public boolean insertCities(List<City> cities) {
        cities.forEach(c -> {
            var regionId = c.getRegionId();
            var region = byRegionId.get(regionId);
            if (region != null) {
                region.addCity(c);
                byCityId.put(c.getId(), region);
            }
        });
        return true;
    }

    @Override
    public boolean insertRegions(List<Region> regions) {
        regions.forEach(r -> byRegionId.put(r.getId(), r));
        return true;
    }

    @Override
    public Optional<Region> getByCityId(int cityId) {
        return Optional.ofNullable(byCityId.get(cityId));
    }

    @Override
    public Optional<Region> getByRegionId(int regionId) {
        return Optional.ofNullable(byRegionId.get(regionId));
    }

    @Override
    public boolean isInitialized() {
        return !byRegionId.isEmpty();
    }
}
