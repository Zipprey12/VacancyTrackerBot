package vacancy_tracker.services.location;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.entity.RegionEntity;
import vacancy_tracker.repository.RegionsRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedRegionsService {

    private final RegionsRepository regionsRepository;
    private final Map<String, RegionEntity> regionByKey = new ConcurrentHashMap<>();
    private final Map<Integer, RegionEntity> regionByCode = new ConcurrentHashMap<>();

    private final Map<Integer, String> additionalRegionNames;

    @PostConstruct
    public void initialize() {
        var dbRegions = regionsRepository.findAll();
        dbRegions.forEach(r -> {
            var key = normalize(r.getName());
            regionByKey.put(key, r);
            regionByCode.put(r.getCode(), r);
        });
        if (additionalRegionNames == null || additionalRegionNames.isEmpty()) {
            return;
        }
        additionalRegionNames.forEach(this::addAdditionalName);
    }

    public Optional<Integer> getCodeByName(String name) {
        var region = regionByKey.get(normalize(name));
        if (region == null) {
            log.warn("Не удалось получить номер региона с названием из базы. " +
                    "Возможно, необходимо добавить дополнительное название: {}", name);
            return Optional.empty();
        }
        return Optional.of(region.getCode());
    }

    public Optional<RegionEntity> getByName(String name) {
        return Optional.ofNullable(regionByKey.get(normalize(name)));
    }

    private void addAdditionalName(int regionCode, String name) {
        var region = regionByCode.get(regionCode);
        if (region == null) {
            throw new IllegalArgumentException("Передан номер региона, которого нет в базе");
        }
        regionByKey.put(normalize(name), region);
    }

    private String normalize(String name) {
        return name.toLowerCase()
                .replaceAll("\\([^)]*+\\)", "")
                .replaceAll("\\s+", " ")
                .strip();
    }
}
