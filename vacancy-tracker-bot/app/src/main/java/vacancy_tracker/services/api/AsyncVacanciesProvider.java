package vacancy_tracker.services.api;

import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.search.VacancySearchFilter;

import java.util.concurrent.CompletableFuture;

public interface AsyncVacanciesProvider {

    VacanciesSource getSource();

    CompletableFuture<VacanciesResponse> find(VacancySearchFilter filter, int limit, int page);

}
