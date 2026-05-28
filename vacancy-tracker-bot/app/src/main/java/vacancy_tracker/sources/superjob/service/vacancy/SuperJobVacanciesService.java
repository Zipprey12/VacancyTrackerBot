package vacancy_tracker.sources.superjob.service.vacancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.ExtendedRegion;
import vacancy_tracker.model.api.Town;
import vacancy_tracker.model.api.VacanciesSource;
import vacancy_tracker.model.api.Vacancy;
import vacancy_tracker.model.api.dto.VacanciesResponse;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.services.api.AsyncVacanciesProvider;
import vacancy_tracker.services.api.location.LocationsServiceImpl;
import vacancy_tracker.sources.superjob.model.response.SuperJobVacanciesResponse;
import vacancy_tracker.sources.superjob.service.company.SuperJobCompanyCacheService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperJobVacanciesService implements AsyncVacanciesProvider {

    public static final VacanciesSource SOURCE = VacanciesSource.SUPER_JOB;

    private final SuperJobVacancyMapper mapper;
    private final SuperJobVacanciesApiClient apiClient;
    private final SuperJobCompanyCacheService companyService;
    private final LocationsServiceImpl locationsService;
    private final Executor vacancySearchExecutor;

    @Override
    public VacanciesSource getSource() {
        return VacanciesSource.SUPER_JOB;
    }

    @Override
    public CompletableFuture<VacanciesResponse> find(VacancySearchFilter filter, int limit, int page) {
        log.info("SuperJob поиск вакансий: {}", filter);

        return CompletableFuture.supplyAsync(() -> {
            var responseOptional = apiClient.searchVacancies(filter, limit, page);

            if (responseOptional.isEmpty()) {
                log.info("Не было найдено вакансий с Super Job");
                var emptyResult = new VacanciesResponse();
                emptyResult.setVacancies(List.of());
                emptyResult.setSource(SOURCE);
                return emptyResult;
            }

            var response = responseOptional.get();
            var result = createResponse(response);
            if (filter.getModifiedFrom() != null) {
                result.setModifiedFrom(filter.getModifiedFrom());
            }
            return result;

        }, vacancySearchExecutor);
    }

    private VacanciesResponse createResponse(SuperJobVacanciesResponse response) {
        var list = response
                .getVacanciesSafe()
                .stream()
                .map(mapper::toEntity)
                .map(this::fillCompany)
                .map(this::fillLocation)
                .toList();

        var result = new VacanciesResponse();
        result.setVacancies(list);
        result.setMore(response.getMore());
        result.setSource(SOURCE);
        result.setTotal(response.getTotal());
        result.setOffset(response.getOffset());
        return result;
    }

    private Vacancy fillCompany(Vacancy vacancy) {
        var id = vacancy.getCompany().getId();
        if (id == null || id <= 0) {
            return vacancy;
        }
        var found = companyService.getCompany(id);
        found.ifPresent(vacancy::setCompany);
        return vacancy;
    }

    private Vacancy fillLocation(Vacancy vacancy) {
        var location = vacancy.getLocation();
        if (location == null) {
            return vacancy;
        }
        var town = location.getTown();
        if (town != null && fillLocation(town, vacancy)) {
            return vacancy;
        }

        var region = location.getRegion();
        if (region != null) {
            fillLocation(region, vacancy);
        }
        return vacancy;
    }

    private boolean fillLocation(Town town, Vacancy vacancy) {
        var found = locationsService.getLocationByTownId(town.getId());
        if (found.isEmpty()) {
            return false;
        }
        vacancy.setLocation(found.get());
        return true;
    }

    private void fillLocation(ExtendedRegion region, Vacancy vacancy) {
        var found = locationsService.getLocationByRegionCode(region.getCode());
        if (found.isEmpty()) {
            return;
        }
        vacancy.setLocation(found.get());
    }
}
