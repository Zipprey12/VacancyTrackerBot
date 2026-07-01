package vacancy_tracker.services.api;

import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.search.VacanciesSearchParams;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface AsyncVacanciesProvider {

    VacanciesSource getSource();

    CompletableFuture<VacanciesResponse> find(VacanciesSearchParams params);

    CompletableFuture<VacanciesResponse> makeTrialResponse(VacanciesSearchParams params);

    default BiConsumer<Integer, Integer> onPublished(long chatId, VacanciesResponse response) {
        return (realMessageId, nextPage) -> {
        };
    }
}
