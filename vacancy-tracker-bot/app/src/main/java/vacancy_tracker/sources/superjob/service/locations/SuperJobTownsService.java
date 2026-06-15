package vacancy_tracker.sources.superjob.service.locations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import vacancy_tracker.model.domain.Town;
import vacancy_tracker.services.api.location.TownsService;
import vacancy_tracker.sources.superjob.model.dto.SuperJobTownDto;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuperJobTownsService implements TownsService {

    private final SuperJobLocationsApiClient apiClient;
    private final SuperJobRegionsConnectingService connectingService;

    @Override
    public Mono<List<Town>> getAll() {
        return apiClient.getAllTowns()
                .map(list -> list.stream()
                        .map(this::map)
                        .filter(Objects::nonNull)
                        .toList());
    }

    private Town map(SuperJobTownDto dto) {
        var code = connectingService.getCodeById(dto.getRegionId());
        return code.map(integer -> Town.builder()
                        .regionCode(integer)
                        .id(dto.getId())
                        .name(dto.getName())
                        .build())
                .orElse(null);
    }
}
