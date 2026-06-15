package vacancy_tracker.sources.superjob.service.locations;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.services.location.SavedRegionsService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuperJobRegionsConnectingService {

    private final SavedRegionsService savedRegionsService;
    private final SuperJobLocationsApiClient apiClient;

    private final Map<Integer, Integer> regionIdByCode = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> regionCodeById = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialize() {
        var regions = apiClient.findAllRegionsWithoutCities().block();
        if (regions == null) return;

        regions.forEach(r -> {
            var number = savedRegionsService.getCodeByName(r.getName());
            number.ifPresent(n -> {
                regionIdByCode.put(n, r.getId());
                regionCodeById.put(r.getId(), n);
            });
        });
    }

    public Optional<Integer> getIdByCode(int number) {
        if (regionIdByCode.containsKey(number)) {
            return Optional.of(regionIdByCode.get(number));
        }
        return Optional.empty();
    }

    public Optional<Integer> getCodeById(int id) {
        if (regionCodeById.containsKey(id)) {
            return Optional.of(regionCodeById.get(id));
        }
        log.warn("Не удалось соединить код региона с id с сайта SuperJob : {}", id);
        return Optional.empty();
    }
}
