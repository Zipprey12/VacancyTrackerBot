package vacancy_tracker.services.api;

import org.springframework.stereotype.Service;
import vacancy_tracker.model.domain.RequestType;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.SearchOutcome;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.search.VacanciesSearchParams;

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
    public CompletableFuture<SearchResult> search(VacanciesSearchParams params) {
        var futures = providers.stream()
                .map(p -> p.find(params))
                .toList();

        return collectResult(params, futures);
    }

    @Override
    public CompletableFuture<SearchResult> search(VacanciesSearchParams data, VacanciesSource source) {
        if (source == null) {
            return search(data);
        }
        var provider = findProvider(source);
        return provider.find(data)
                .thenApply(response ->
                        createResult(response, data.getFilter().getRequestType()));
    }

    @Override
    public CompletableFuture<SearchOutcome> searchWithOutcome(VacanciesSearchParams data, VacanciesSource source) {
        if (source == null) {
            return search(data).thenApply(result -> {
                var responses = result.getNotEmptyResponses();
                if (responses.isEmpty()) {
                    return SearchOutcome.withoutCallback(result);
                }
                var first = responses.getFirst();
                var provider = findProvider(first.getSource());

                var onPublished = provider.onPublished(data.getChatId(), first);
                return new SearchOutcome(result, onPublished);
            });
        }

        var provider = findProvider(source);
        return provider.find(data)
                .thenApply(response -> {
                    var result = createResult(response, data.getFilter().getRequestType());
                    var onPublished = provider.onPublished(data.getChatId(), response);
                    return new SearchOutcome(result, onPublished);
                });
    }

    @Override
    public CompletableFuture<SearchResult> makeTrialRequest(VacanciesSearchParams params) {
        params.setLimit(1);
        params.setPage(0);

        List<CompletableFuture<VacanciesResponse>> futures = providers.stream()
                .map(p -> p.makeTrialResponse(params))
                .toList();
        return collectResult(params, futures);
    }

    private CompletableFuture<SearchResult> collectResult(VacanciesSearchParams params, List<CompletableFuture<VacanciesResponse>> futures) {
        var filter = params.getFilter();
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

    private AsyncVacanciesProvider findProvider(VacanciesSource source) {
        var provider = providerBySource.get(source);
        if (provider == null) {
            throw new IllegalArgumentException("Не найден источник данных: " + source.getName());
        }
        return provider;
    }

    private SearchResult createResult(VacanciesResponse response, RequestType type) {
        var result = new SearchResult(type);
        result.addResponse(response);
        response.setModifiedFrom(response.getModifiedFrom());
        return result;
    }
}
