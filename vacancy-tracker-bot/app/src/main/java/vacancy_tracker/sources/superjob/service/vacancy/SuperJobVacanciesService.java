package vacancy_tracker.sources.superjob.service.vacancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.vacancy.Vacancy;
import vacancy_tracker.model.vacancy.dto.VacancySearchFilterDto;
import vacancy_tracker.services.vacancy.VacancyService;
import vacancy_tracker.sources.superjob.model.SuperJobVacancyDto;
import vacancy_tracker.sources.superjob.service.company.SuperJobCompanyCacheService;

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

    @Override
    public String getSourceName() {
        return "superjob";
    }

    @Override
    public CompletableFuture<List<Vacancy>> search(VacancySearchFilterDto filter) {
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
                    .toList();
        });
    }

    public void fillCompany(Vacancy vacancy){
        var id = vacancy.getCompany().getId();
        if(id == null){
            return;
        }
        var found = companyService.getCompany(id);
        found.ifPresent(vacancy::setCompany);
    }

    @Override
    public boolean isAvailable() {
        VacancySearchFilterDto testFilter = VacancySearchFilterDto.builder()
                .text("java")
                .limit(1)
                .build();
        return apiClient.searchVacancies(testFilter).isPresent();
    }

    @Override
    public int getPriority() {
        return 0;
    }

}