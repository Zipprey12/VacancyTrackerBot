package vacancy_tracker.sources.superjob.service.company;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import vacancy_tracker.model.domain.Company;
import vacancy_tracker.sources.superjob.model.response.SuperJobCompanyResponse;
import vacancy_tracker.sources.superjob.repository.SuperJobCompaniesRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SuperJobCompanyCacheService {

    private static final int CACHE_UPDATE_DURATION_IN_MINUTES = 60;

    private final SuperJobCompaniesRepository companyRepository;
    private final SuperJobCompaniesApiClient companiesClient;
    private final SuperJobCompanyCacheService self;

    public SuperJobCompanyCacheService(SuperJobCompaniesRepository companyRepository,
                                       SuperJobCompaniesApiClient companiesClient,
                                       @Lazy SuperJobCompanyCacheService self) {
        this.companyRepository = companyRepository;
        this.companiesClient = companiesClient;
        this.self = self;
    }

    @Async
    public void refreshCompany(int id) {
        fetchAndCacheCompany(id).subscribe(
                company -> log.debug("Данные компании {} обновлены", id),
                throwable -> log.warn("Ошибка обновления данных компании {}", id)
        );
    }

    public Mono<Company> getCompany(int id) {
        var cached = companyRepository.findById(id);
        if (cached.isPresent()) {
            var company = cached.get();
            if (isUpdateNeeded(company.getLastUpdateAt())) {
                self.refreshCompany(id);
            }
            return Mono.just(company);
        }
        return fetchAndCacheCompany(id);
    }

    private Company saveCompanyFromApi(SuperJobCompanyResponse apiCompany) {
        Company company = companyRepository
                .findById(apiCompany.getId())
                .orElseGet(() -> Company.builder()
                        .id(apiCompany.getId())
                        .build());

        company.setName(apiCompany.getTitle() != null ? apiCompany.getTitle() : "ID: " + apiCompany.getId());
        company.setUrl(apiCompany.getLink());
        company.setLastUpdateAt(Timestamp.valueOf(LocalDateTime.now()));

        return companyRepository.save(company);
    }

    private Mono<Company> fetchAndCacheCompany(long id) {
        return companiesClient.getCompanyById(id)
                .map(this::saveCompanyFromApi);
    }

    private boolean isUpdateNeeded(Timestamp timestamp) {
        return timestamp.before(Timestamp.valueOf(LocalDateTime.now()
                .minusMinutes(CACHE_UPDATE_DURATION_IN_MINUTES)));
    }
}
