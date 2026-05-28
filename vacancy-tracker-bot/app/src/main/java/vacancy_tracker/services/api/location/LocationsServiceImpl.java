package vacancy_tracker.services.api.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.ExtendedRegion;
import vacancy_tracker.model.api.Location;
import vacancy_tracker.repository.LocationsRepository;
import vacancy_tracker.sources.superjob.service.locations.SuperJobTownsService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service("locationsService")
public class LocationsServiceImpl implements LocationsService {

    private final LocationsRepository repository;
    private final SuperJobTownsService townsService;

    public LocationsServiceImpl(LocationsRepository repository,
                                SuperJobTownsService townsService) {
        this.repository = repository;
        this.townsService = townsService;
    }

    @Override
    public void initialize() {
        var towns = townsService.getAll();
        repository.insertTowns(towns);
    }

    @Override
    public Optional<ExtendedRegion> getRegionByCode(int code) {
        return repository.getRegionByCode(code);
    }

    @Override
    public Optional<Location> getLocationByRegionCode(int code) {
        var region = repository.getRegionByCode(code);
        if (region.isEmpty()) {
            return Optional.empty();
        }
        var location = new Location();
        location.setRegion(region.get());
        return Optional.of(location);
    }

    @Override
    public Optional<Location> getLocationByTownId(int townId) {
        var town = repository.getTownById(townId);
        if (town.isEmpty()) {
            return Optional.empty();
        }
        var location = new Location();
        location.setTown(town.get());

        var regionId = town.get().getRegionCode();
        var region = repository.getRegionBasicByCode(regionId);
        region.ifPresent(location::setRegion);

        return Optional.of(location);
    }

    @Override
    public List<ExtendedRegion> getAllRegionsBasic() {
        return repository.getAllRegionsBasic();
    }
}
