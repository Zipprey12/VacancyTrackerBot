package vacancy_tracker.sources.trudvsem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.api.entity.Vacancy;
import vacancy_tracker.services.vacancy.VacancyService;
import vacancy_tracker.sources.trudvsem.model.TrudVsemResponse;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrudVsemVacanciesService implements VacancyService {

    private final TrudVsemApiClient apiClient;

    @Override
    public String getSourceName() {
        return "Trud vsem";
    }

    @Async
    @Override
    public CompletableFuture<List<Vacancy>> search(VacancySearchFilter filter) {
        return apiClient.searchVacancies(filter)
                .map(TrudVsemResponse::getVacancies)
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.completedFuture(Collections.emptyList()));
    }

    @Override
    public boolean isAvailable() {
        return apiClient.ping();
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
