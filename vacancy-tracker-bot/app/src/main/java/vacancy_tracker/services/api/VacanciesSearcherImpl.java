package vacancy_tracker.services.api;

import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.VacanciesSource;
import vacancy_tracker.model.api.dto.SearchResult;
import vacancy_tracker.model.api.dto.VacanciesResponse;
import vacancy_tracker.model.api.dto.VacancySearchFilter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class VacanciesSearcherImpl implements VacanciesSearcher {

    private final List<AsyncVacanciesProvider> providers;
    private final Map<VacanciesSource, AsyncVacanciesProvider> providerBySource;

    public VacanciesSearcherImpl(List<AsyncVacanciesProvider> providers) {
        this.providers = providers;
        this.providerBySource = new EnumMap<>(VacanciesSource.class);
        providers.forEach(p -> providerBySource.put(p.getSource(), p));
    }

    @Override
    public CompletableFuture<SearchResult> search(VacancySearchFilter filter, int limit, int page) {
        List<CompletableFuture<VacanciesResponse>> futures = providers.stream()
                .map(p -> p.find(filter, limit, page))
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> {
                    var result = new SearchResult(filter.getRequestType());
                    futures.stream()
                            .map(CompletableFuture::join)
                            .forEach(result::addResponse);
                    return result;
                });
    }

    @Override
    public CompletableFuture<SearchResult> search(VacancySearchFilter filter, int limit, int page, VacanciesSource source) {
        if (source == null) {
            return search(filter, limit, page);
        }
        var provider = providerBySource.get(source);
        if (provider == null) {
            throw new IllegalArgumentException("Не найден источник данных: " + source.getName());
        }
        return provider.find(filter, limit, page)
                .thenApply(response -> {
                    SearchResult result = new SearchResult(filter.getRequestType());
                    result.addResponse(response);
                    return result;
                });
    }
}
