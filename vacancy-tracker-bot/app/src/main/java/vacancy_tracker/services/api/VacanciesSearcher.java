package vacancy_tracker.services.api;

import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacancySearchFilter;

import java.util.concurrent.CompletableFuture;

public interface VacanciesSearcher {

    CompletableFuture<SearchResult> search(VacancySearchFilter filter, int limit, int page);

    CompletableFuture<SearchResult> search(VacancySearchFilter filter, int limit, int page, VacanciesSource source);
}
