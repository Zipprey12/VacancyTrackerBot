package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.repository.LocationsRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryLocationRepository implements LocationsRepository {

    private final Map<Integer, Region> byCityId = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Region> byRegionId = new LinkedHashMap<>();
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
        regions.forEach(region -> {
            byRegionId.put(region.getId(), region);
            if (region.getTowns() != null) {
                region.getTowns().forEach(town -> {
                    townsById.put(town.getId(), town);
                    byCityId.put(town.getId(), region);
                });
            }
        });
    }

    @Override
    public Optional<Region> getRegionByTownId(int townId) {
        var existed = byCityId.get(townId);
        return toOptional(copy(existed));
    }

    @Override
    public Optional<Region> getRegionById(int regionId) {
        var existed = byRegionId.get(regionId);
        return toOptional(copy(existed));
    }

    @Override
    public Optional<Region> getRegionBasicById(int regionId) {
        var existed = byRegionId.get(regionId);
        return toOptional(copyBasic(existed));
    }

    @Override
    public Optional<Town> getTownById(int townId) {
        var existed = townsById.get(townId);
        return toOptional(copy(existed));
    }

    @Override
    public List<Region> getAllRegionsBasic() {
        return byRegionId.sequencedValues().stream()
                .map(this::copy)
                .toList();
    }

    @Override
    public boolean isInitialized() {
        return !byRegionId.isEmpty();
    }

    private <T> Optional<T> toOptional(T source) {
        return Optional.ofNullable(source);
    }

    private Region copy(Region source) {
        List<Town> townsCopy = source.getTowns() == null
                ? null
                : source.getTowns().stream()
                .map(this::copy)
                .toList();

        var copy = copyBasic(source);
        copy.setTowns(townsCopy);
        return copy;
    }

    private Town copy(Town source) {
        return Town.builder()
                .id(source.getId())
                .name(source.getName())
                .regionId(source.getRegionId())
                .build();
    }

    private Region copyBasic(Region source) {
        return Region.builder()
                .name(source.getName())
                .id(source.getId())
                .build();
    }
}
