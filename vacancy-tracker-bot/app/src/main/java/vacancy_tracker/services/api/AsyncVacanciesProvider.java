package vacancy_tracker.services.api;

import vacancy_tracker.model.api.VacanciesSource;
import vacancy_tracker.model.api.dto.VacanciesResponse;
import vacancy_tracker.model.api.dto.VacancySearchFilter;

import java.util.concurrent.CompletableFuture;

public interface AsyncVacanciesProvider {

    VacanciesSource getSource();

    CompletableFuture<VacanciesResponse> find(VacancySearchFilter filter, int limit, int page);

}
