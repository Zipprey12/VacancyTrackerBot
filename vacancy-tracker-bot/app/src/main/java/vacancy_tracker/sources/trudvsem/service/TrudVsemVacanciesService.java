package vacancy_tracker.sources.trudvsem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.services.api.AsyncVacanciesProvider;
import vacancy_tracker.sources.trudvsem.model.TrudVsemResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrudVsemVacanciesService implements AsyncVacanciesProvider {

    private static final VacanciesSource SOURCE = VacanciesSource.TRUD_VSEM;
    private final TrudVsemApiClient apiClient;
    private final TrudVsemVacancyMapper mapper;

    @Override
    public VacanciesSource getSource() {
        return VacanciesSource.TRUD_VSEM;
    }

    @Override
    public CompletableFuture<VacanciesResponse> find(VacancySearchFilter filter, int limit, int page) {
        log.info("TrudVsem: получение вакансий: {}", filter);

        return apiClient.searchVacancies(filter, limit, page)
                .map(tr -> {
                    var response = createResponse(tr, limit, page * limit);
                    response.setModifiedFrom(filter.getModifiedFrom());
                    return response;
                })
                .switchIfEmpty(Mono.fromCallable(this::createEmptyResponse))
                .toFuture();
    }

    private VacanciesResponse createEmptyResponse() {
        log.info("Не было найдено вакансий с TrudVsem");
        return VacanciesResponse.builder()
                .vacancies(List.of())
                .source(SOURCE)
                .build();
    }

    private VacanciesResponse createResponse(TrudVsemResponse response, int limit, int offset) {
        var vacancies = response.getVacanciesSafe()
                .stream()
                .map(mapper::toEntity)
                .toList();
        var result = new VacanciesResponse();
        var total = response.getMeta().getTotal();
        var isMore = (offset + limit) < total;

        result.setVacancies(vacancies);
        result.setMore(isMore);
        result.setTotal(total);
        result.setSource(SOURCE);
        result.setOffset(offset);
        return result;
    }
}
