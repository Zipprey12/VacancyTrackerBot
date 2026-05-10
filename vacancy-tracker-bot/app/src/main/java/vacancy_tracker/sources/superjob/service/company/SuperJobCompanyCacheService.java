package vacancy_tracker.sources.superjob.service.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.entity.Company;
import vacancy_tracker.sources.superjob.model.response.SuperJobCompanyResponse;
import vacancy_tracker.sources.superjob.repository.SuperJobCompaniesRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperJobCompanyCacheService {

    private final SuperJobCompaniesRepository companyRepository;
    private final SuperJobCompaniesApiClient companiesClient;

    //todo
    @Lazy
    private SuperJobCompanyCacheService self;

    private static final int CACHE_UPDATE_DURATION_IN_MINUTES = 10;

    public Optional<Company> getCompany(int id) {

        var cached = companyRepository.findById(id);

        if (cached.isPresent()) {
            var company = cached.get();

            if (isUpdateNeeded(company.getLastUpdateAt())) {
                self.refreshCompanyAsync(id);
            }
            return Optional.of(company);
        }

        return fetchAndCacheCompany(id);
    }

    private Optional<Company> fetchAndCacheCompany(long id) {
        return companiesClient.getCompanyById(id)
                .map(this::saveCompanyFromApi);
    }

    protected Company saveCompanyFromApi(SuperJobCompanyResponse apiCompany) {
        Company company = companyRepository
                .findById(apiCompany.getId())
                .orElseGet(() -> Company.builder()
                        .id(apiCompany.getId())
                        .build());

        company.setName(apiCompany.getTitle() != null ? apiCompany.getTitle() : "ID: " + apiCompany.getId());
        company.setLink(apiCompany.getLink());
        company.setLastUpdateAt(Timestamp.valueOf(LocalDateTime.now()));

        return companyRepository.save(company);
    }

    private boolean isUpdateNeeded(Timestamp timestamp) {
        return timestamp.before(Timestamp.valueOf(LocalDateTime.now()
                .minusMinutes(CACHE_UPDATE_DURATION_IN_MINUTES)));
    }

    @Async
    public CompletableFuture<Void> refreshCompanyAsync(int id) {
        fetchAndCacheCompany(id);
        return CompletableFuture.completedFuture(null);
    }
}
