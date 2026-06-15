package vacancy_tracker.services.api;

import org.springframework.stereotype.Service;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.search.VacancySearchFilter;

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

        var modifiedFrom = filter.getModifiedFrom();
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> {
                    var res = new SearchResult(filter.getRequestType());
                    futures.stream()
                            .map(CompletableFuture::join)
                            .forEach(r -> {
                                r.setModifiedFrom(modifiedFrom);
                                res.addResponse(r);
                            });
                    res.setModifiedFrom(modifiedFrom);
                    return res;
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
                    response.setModifiedFrom(response.getModifiedFrom());
                    return result;
                });
    }
}
