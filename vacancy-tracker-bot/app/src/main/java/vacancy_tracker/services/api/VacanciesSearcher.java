package vacancy_tracker.services.api;

import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.SearchOutcome;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesSearchParams;

import java.util.concurrent.CompletableFuture;

public interface VacanciesSearcher {

    CompletableFuture<SearchResult> search(VacanciesSearchParams params);

    CompletableFuture<SearchResult> search(VacanciesSearchParams params, VacanciesSource source);

    CompletableFuture<SearchOutcome> searchWithOutcome(VacanciesSearchParams params, VacanciesSource source);

    CompletableFuture<SearchResult> makeTrialRequest(VacanciesSearchParams params);
}
