package vacancy_tracker.services.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.*;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SearchVacanciesService {

    public static final int VACANCIES_COUNT_LIMIT = 10;

    private final VacanciesSearcher searcher;

    public CompletableFuture<SearchResult> makeTrialRequest(VacancySearchFilter filter, long chatId, VacanciesSearchRequest request) {
        filter.setRequestType(request.getRequestType());
        filter.setModifiedFrom(request.getStartDate());

        var params = VacanciesSearchParams.builder()
                .filter(filter)
                .chatId(chatId)
                .build();

        return searcher.makeTrialRequest(params);
    }

    public CompletableFuture<SearchOutcome> searchWithOutcome(VacanciesSearchParams params, VacanciesSource source) {
        return searcher.searchWithOutcome(params, source);
    }
}
