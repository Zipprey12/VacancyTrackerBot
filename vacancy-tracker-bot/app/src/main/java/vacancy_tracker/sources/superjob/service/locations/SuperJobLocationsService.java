package vacancy_tracker.sources.superjob.service.locations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.vacancy.entity.Region;
import vacancy_tracker.repository.LocationsRepository;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuperJobLocationsService implements LocationsService {

    private final LocationsRepository repository;
    private final SuperJobLocationsApiClient apiClient;

    @Override
    public void initialize() {
        if (repository.isInitialized()) {
            return;
        }

        var regions = apiClient.findAllRegionsWithoutCities();
        repository.insertRegions(regions);

        var cities = apiClient.getAllCities();
        repository.insertCities(cities);
    }

    @Override
    public Optional<Region> getByCityId(int cityId) {
        return repository.getByCityId(cityId);
    }

    @Override
    public Optional<Region> getByRegionId(int regionId) {
        return repository.getByRegionId(regionId);
    }

    @Override
    public boolean isInitialized() {
        return repository.isInitialized();
    }
}
