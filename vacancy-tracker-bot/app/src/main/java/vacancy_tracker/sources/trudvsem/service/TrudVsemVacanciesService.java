package vacancy_tracker.sources.trudvsem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.api.VacanciesSource;
import vacancy_tracker.model.api.dto.VacanciesResponse;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.services.api.AsyncVacanciesProvider;
import vacancy_tracker.sources.trudvsem.model.TrudVsemResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
@RequiredArgsConstructor
//todo вынести общую логику в abstract class
public class TrudVsemVacanciesService implements AsyncVacanciesProvider {

    private static final VacanciesSource SOURCE = VacanciesSource.TRUD_VSEM;
    private final TrudVsemApiClient apiClient;
    private final TrudVsemVacancyMapper mapper;
    private final Executor vacancySearchExecutor;

    @Override
    public VacanciesSource getSource() {
        return VacanciesSource.TRUD_VSEM;
    }

    @Override
    public CompletableFuture<VacanciesResponse> find(VacancySearchFilter filter, int limit, int page) {
        log.info("TrudVsem: получение вакансий: {}", filter);

        return CompletableFuture.supplyAsync(() -> {
            var responseOptional = apiClient.searchVacancies(filter, limit, page);

            if (responseOptional.isEmpty()) {
                log.info("Не было найдено вакансий с TrudVsem");
                var emptyResult = new VacanciesResponse();
                emptyResult.setVacancies(List.of());
                emptyResult.setSource(SOURCE);
                return emptyResult;
            }

            var response = responseOptional.get();
            return createResponse(response, limit, page * limit);
        }, vacancySearchExecutor);
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
