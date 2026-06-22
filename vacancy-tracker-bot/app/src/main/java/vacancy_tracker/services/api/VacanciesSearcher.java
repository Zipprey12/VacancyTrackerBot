package vacancy_tracker.services.api;

import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.SearchOutcome;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesSearchData;

import java.util.concurrent.CompletableFuture;

public interface VacanciesSearcher {

    CompletableFuture<SearchResult> search(VacanciesSearchData data);

    CompletableFuture<SearchResult> search(VacanciesSearchData data, VacanciesSource source);

    CompletableFuture<SearchOutcome> searchWithOutcome(VacanciesSearchData data, VacanciesSource source);
}
