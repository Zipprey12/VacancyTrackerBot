package vacancy_tracker.sources.superjob.service.vacancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.model.api.entity.Vacancy;
import vacancy_tracker.services.vacancy.VacancyService;
import vacancy_tracker.sources.superjob.service.company.SuperJobCompanyCacheService;
import vacancy_tracker.sources.superjob.service.locations.SuperJobLocationsService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperJobVacanciesService implements VacancyService {

    private final SuperJobVacancyMapper mapper;
    private final SuperJobVacanciesApiClient apiClient;

    private final SuperJobCompanyCacheService companyService;
    private final SuperJobLocationsService locationsService;

    @Override
    public String getSourceName() {
        return "superjob";
    }

    @Override
    public CompletableFuture<List<Vacancy>> search(VacancySearchFilter filter) {
        log.info("Searching vacancies for query: {}", filter);

        return CompletableFuture.supplyAsync(() -> {
            log.info("Executing in thread: {}", Thread.currentThread().getName());

            var vacancies = apiClient.searchVacancies(filter);

            if (vacancies.isEmpty()) {
                log.info("Не было найдено вакансий с Super Job");
                return Collections.emptyList();
            }

            return vacancies.get()
                    .getVacanciesSafe()
                    .stream()
                    .map(mapper::toEntity)
                    .peek(this::fillCompany)
                    .peek(this::fillLocation)
                    .toList();
        });
    }

    @Override
    public boolean isAvailable() {
        VacancySearchFilter testFilter = VacancySearchFilter.builder()
                .text("java")
                .limit(1)
                .build();
        return apiClient.searchVacancies(testFilter).isPresent();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    //todo
    public void fillCompany(Vacancy vacancy) {
        var id = vacancy.getCompany().getId();
        if (id == null) {
            return;
        }
        var found = companyService.getCompany(id);
        found.ifPresent(vacancy::setCompany);
    }

    private void fillLocation(Vacancy vacancy) {
        var location = vacancy.getLocation();
        if (location == null) {
            return;
        }
        var town = location.getTown();
        if (town != null && fillLocation(town, vacancy)) {
            return;
        }

        var region = location.getRegion();
        if (region != null) {
            fillLocation(region, vacancy);
        }
    }

    private boolean fillLocation(Town town, Vacancy vacancy) {
        var found = locationsService.getLocationByTownId(town.getId());
        if (found.isEmpty()) {
            return false;
        }
        vacancy.setLocation(found.get());
        return true;
    }

    private void fillLocation(Region region, Vacancy vacancy) {
        var found = locationsService.getLocationByRegionId(region.getId());
        if (found.isEmpty()) {
            return;
        }
        vacancy.setLocation(found.get());
    }
}
