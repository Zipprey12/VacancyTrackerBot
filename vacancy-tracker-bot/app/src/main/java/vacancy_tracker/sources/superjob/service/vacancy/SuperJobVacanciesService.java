package vacancy_tracker.sources.superjob.service.vacancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vacancy_tracker.model.domain.Region;
import vacancy_tracker.model.domain.Town;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.domain.Vacancy;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.services.api.AsyncVacanciesProvider;
import vacancy_tracker.services.api.location.LocationsServiceImpl;
import vacancy_tracker.sources.superjob.model.response.SuperJobVacanciesResponse;
import vacancy_tracker.sources.superjob.service.company.SuperJobCompanyCacheService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperJobVacanciesService implements AsyncVacanciesProvider {

    public static final VacanciesSource SOURCE = VacanciesSource.SUPER_JOB;

    private final SuperJobVacancyMapper mapper;
    private final SuperJobVacanciesApiClient apiClient;
    private final SuperJobCompanyCacheService companyService;
    private final LocationsServiceImpl locationsService;

    @Override
    public VacanciesSource getSource() {
        return VacanciesSource.SUPER_JOB;
    }

    @Override
    public CompletableFuture<VacanciesResponse> find(VacancySearchFilter filter, int limit, int page) {
        log.info("SuperJob поиск вакансий: {}", filter);
        return apiClient.searchVacancies(filter, limit, page)
                .flatMap(this::createResponse)
                .switchIfEmpty(Mono.fromCallable(this::createEmptyResponse))
                .doOnSuccess(r -> {
                    r.setModifiedFrom(filter.getModifiedFrom());
                    log.debug("SuperJob: возвращено {} вакансий", r.getVacancies().size());
                })
                .toFuture();
    }

    private VacanciesResponse createEmptyResponse() {
        log.debug("Не было найдено вакансий с SuperJob");
        var response = new VacanciesResponse();
        response.setVacancies(List.of());
        response.setSource(SOURCE);
        return response;
    }

    private Mono<VacanciesResponse> createResponse(SuperJobVacanciesResponse response) {
        var vacancies = response.getVacanciesSafe()
                .stream()
                .map(mapper::toEntity)
                .toList();

        if (vacancies.isEmpty()) {
            return Mono.just(buildResponse(List.of(), response));
        }

        return Flux.fromIterable(vacancies)
                .flatMap(this::fillCompany, 3)
                .map(this::fillLocation)
                .collectList()
                .map(list -> buildResponse(list, response));
    }

    private VacanciesResponse buildResponse(List<Vacancy> vacancies,
                                            SuperJobVacanciesResponse response) {
        return VacanciesResponse.builder()
                .vacancies(vacancies)
                .more(response.getMore())
                .source(SOURCE)
                .total(response.getTotal())
                .offset(response.getOffset())
                .build();
    }

    private Mono<Vacancy> fillCompany(Vacancy vacancy) {
        var id = vacancy.getCompany().getId();
        if (id == null || id <= 0) {
            return Mono.just(vacancy);
        }
        return companyService.getCompany(id)
                .map(company -> {
                    vacancy.setCompany(company);
                    return vacancy;
                })
                .onErrorResume(e -> {
                    log.warn("Не удалось получить компанию {}: {}", id, e.getMessage());
                    vacancy.getCompany().setName("Компания " + id);
                    return Mono.just(vacancy);
                });
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

    private void fillLocation(Region region, Vacancy vacancy) {
        var found = locationsService.getLocationByRegionCode(region.getCode());
        if (found.isEmpty()) {
            return;
        }
        vacancy.setLocation(found.get());
    }
}
