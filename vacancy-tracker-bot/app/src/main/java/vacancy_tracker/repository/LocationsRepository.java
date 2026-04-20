package vacancy_tracker.repository;

import vacancy_tracker.model.vacancy.entity.City;
import vacancy_tracker.model.vacancy.entity.Region;

import java.util.List;
import java.util.Optional;

public interface LocationsRepository {

    boolean insertCities(List<City> cities);

    boolean insertRegions(List<Region> regions);

    Optional<Region> getByCityId(int cityId);

    Optional<Region> getByRegionId(int regionId);

    boolean isInitialized();
}
