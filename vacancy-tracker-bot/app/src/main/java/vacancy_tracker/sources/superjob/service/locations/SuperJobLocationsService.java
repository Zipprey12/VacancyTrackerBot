package vacancy_tracker.sources.superjob.service.locations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.model.api.entity.Region;
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
        repository.insertTowns(cities);
    }

    @Override
    public Optional<Region> getRegionByTownId(int townId) {
        return repository.getRegionByTownId(townId);
    }

    @Override
    public Optional<Region> getRegionById(int regionId) {
        return repository.getRegionById(regionId);
    }

    @Override
    public Optional<Location> getLocationByRegionId(int regionId) {
        var region = repository.getRegionBasicById(regionId);
        if(region.isEmpty()){
            return Optional.empty();
        }
        var location = new Location();
        location.setRegion(region.get());
        return Optional.of(location);
    }

    @Override
    public Optional<Location> getLocationByTownId(int townId) {
        var town = repository.getTownById(townId);
        if(town.isEmpty()){
            return Optional.empty();
        }
        var location = new Location();
        location.setTown(town.get());

        var regionId = town.get().getRegionId();
        var region = repository.getRegionBasicById(regionId);
        region.ifPresent(location::setRegion);

        return Optional.of(location);
    }

    @Override
    public boolean isInitialized() {
        return repository.isInitialized();
    }
}
