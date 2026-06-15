package vacancy_tracker.repository.in_memory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import vacancy_tracker.model.domain.Region;
import vacancy_tracker.model.domain.Town;
import vacancy_tracker.repository.LocationsRepository;
import vacancy_tracker.repository.RegionsRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryLocationRepository implements LocationsRepository {

    private final Map<Integer, Region> byCityId = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Region> byRegionCode = new LinkedHashMap<>();
    private final Map<Integer, Town> townsById = new LinkedHashMap<>();

    private final RegionsRepository regionsRepository;

    @PostConstruct
    public void initialize() {
        var regions = regionsRepository.findAll();
        regions.forEach(r -> {
            var code = r.getCode();
            var region = new Region();
            region.setCode(code);
            region.setName(r.getName());
            byRegionCode.put(r.getCode(), region);
        });
    }

    @Override
    public void insertTowns(List<Town> towns) {
        towns.forEach(t -> {
            townsById.put(t.getId(), t);

            var regionId = t.getRegionCode();
            var region = byRegionCode.get(regionId);
            if (region != null) {
                region.addCity(t);
                byCityId.put(t.getId(), region);
            }
        });
    }

    @Override
    public void insertRegions(List<Region> regions) {
        regions.forEach(region -> {
            byRegionCode.put(region.getCode(), region);
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
    public Optional<Region> getRegionByCode(int code) {
        var existed = byRegionCode.get(code);
        return toOptional(copy(existed));
    }

    @Override
    public Optional<Region> getRegionBasicByCode(int code) {
        var existed = byRegionCode.get(code);
        return toOptional(copyBasic(existed));
    }

    @Override
    public Optional<Town> getTownById(int townId) {
        var existed = townsById.get(townId);
        return toOptional(copy(existed));
    }

    @Override
    public List<Region> getAllRegionsBasic() {
        return byRegionCode.sequencedValues().stream()
                .map(this::copy)
                .toList();
    }

    @Override
    public boolean isInitialized() {
        return !byRegionCode.isEmpty();
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
                .regionCode(source.getRegionCode())
                .build();
    }

    private Region copyBasic(Region source) {
        return Region.builder()
                .name(source.getName())
                .code(source.getCode())
                .build();
    }
}
