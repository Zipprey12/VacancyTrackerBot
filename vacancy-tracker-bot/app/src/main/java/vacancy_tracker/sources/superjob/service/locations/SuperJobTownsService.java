package vacancy_tracker.sources.superjob.service.locations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.Town;
import vacancy_tracker.services.api.location.TownsService;
import vacancy_tracker.sources.superjob.model.dto.SuperJobTownDto;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuperJobTownsService implements TownsService {

    private final SuperJobLocationsApiClient apiClient;
    private final SuperJobRegionsConnectingService connectingService;

    @Override
    public List<Town> getAll() {
        var dtoList = apiClient.getAllTowns();
        List<Town> towns = new LinkedList<>();
        for (var dto : dtoList) {
            var mapped = map(dto);
            if (mapped != null) {
                towns.add(mapped);
            }
        }
        return towns;
    }

    private Town map(SuperJobTownDto dto) {
        var code = connectingService.getCodeById(dto.getRegionId());
        if (code.isEmpty()) {
            return null;
        }
        var town = new Town();
        town.setRegionCode(code.get());
        town.setId(dto.getId());
        town.setName(dto.getName());
        return town;
    }
}
