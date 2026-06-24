package vacancy_tracker.sources.trudvsem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.CollectedBatch;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.search.VacanciesSearchParams;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.services.api.AsyncVacanciesProvider;
import vacancy_tracker.services.telegram.pagination.VacanciesPaginationOffsetService;
import vacancy_tracker.sources.trudvsem.model.TrudVsemResponse;
import vacancy_tracker.sources.trudvsem.model.dto.TrudVsemVacancyDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrudVsemVacanciesService implements AsyncVacanciesProvider {

    private static final int SALARY_FILTER_BATCH_SIZE = 100;
    private static final int MAX_BATCHES_PER_REQUEST = 10;

    private static final VacanciesSource SOURCE = VacanciesSource.TRUD_VSEM;

    private final VacanciesPaginationOffsetService paginationOffsetService;
    private final TrudVsemApiClient apiClient;
    private final TrudVsemVacancyMapper mapper;

    @Override
    public VacanciesSource getSource() {
        return VacanciesSource.TRUD_VSEM;
    }

    @Override
    public CompletableFuture<VacanciesResponse> find(VacanciesSearchParams params) {
        var filter = params.getFilter();
        var limit = params.getLimit();
        var page = params.getPage();

        log.info("TrudVsem: получение вакансий: {}", filter);

        if (hasSalaryFilter(filter)) {
            var offsetArgs = paginationOffsetService.resolveStartOffset(params.getChatId(), params.getMessageId(), page);
            return findWithSalaryFilter(filter, limit, offsetArgs.getOffset())
                    .doOnSuccess(response -> response.setPage(offsetArgs.getPage()))
                    .toFuture();
        }
        return searchWithoutSalaryFilter(params);
    }

    @Override
    public CompletableFuture<VacanciesResponse> makeTrialResponse(VacanciesSearchParams data) {
        return searchWithoutSalaryFilter(data);
    }

    @Override
    public BiConsumer<Integer, Integer> onPublished(long chatId, VacanciesResponse response) {
        if (!SOURCE.equals(response.getSource())) {
            return (realMessageId, nextPage) -> {
            };
        }
        return (sendMessageId, nextPage) -> {
            if (sendMessageId == null) {
                return;
            }
            paginationOffsetService.saveNextPageOffset(chatId, sendMessageId, nextPage, response.getOffset());
        };
    }

    private CompletableFuture<VacanciesResponse> searchWithoutSalaryFilter(VacanciesSearchParams params){
        var filter = params.getFilter();
        var limit = params.getLimit();
        var page = params.getPage();

        return apiClient.searchVacancies(filter, limit, page)
                .map(tr -> {
                    var response = createResponse(tr, limit, page);
                    response.setModifiedFrom(filter.getModifiedFrom());
                    return response;
                })
                .switchIfEmpty(Mono.fromCallable(this::createEmptyResponse))
                .toFuture();
    }

    private boolean hasSalaryFilter(VacancySearchFilter filter) {
        return filter.getMinSalary() != null || filter.getMaxSalary() != null;
    }

    private Mono<VacanciesResponse> findWithSalaryFilter(VacancySearchFilter filter, int limit, int offset) {
        var batch = new CollectedBatch();

        return collectMatching(filter, limit, offset, batch, 0)
                .map(collected -> {
                    var response = createResponse(collected, filter, limit);
                    var more = !batch.isSourceExhausted();
                    response.setCanHasOther(offset != 0 && more || !batch.getMatched().isEmpty());
                    response.setMore(more);
                    response.setOffset(batch.getNextOffset());
                    response.setCanHasOther(!batch.isSourceExhausted());
                    return response;
                });
    }

    private Mono<CollectedBatch> collectMatching(VacancySearchFilter filter,
                                                 int limit,
                                                 int offset,
                                                 CollectedBatch batch,
                                                 int batchCount) {
        var found = batch.getMatched();
        if (batchCount >= MAX_BATCHES_PER_REQUEST) {
            log.warn("TrudVsem: достигнут лимит {} запросов при поиске с фильтром по зарплате, " +
                    "найдено {} из {}", MAX_BATCHES_PER_REQUEST, found.size(), limit);
            batch.setNextOffset(offset);
            batch.setSourceExhausted(false);
            return Mono.just(batch);
        }

        var page = offset / SALARY_FILTER_BATCH_SIZE;
        int batchOffset = page * SALARY_FILTER_BATCH_SIZE;
        int toSkip = offset % SALARY_FILTER_BATCH_SIZE;

        batch.setNextPage(page);
        return apiClient.searchVacancies(filter, SALARY_FILTER_BATCH_SIZE, page)
                .flatMap(response -> processBatch(filter, limit, batch, batchCount, response, batchOffset, toSkip))
                .switchIfEmpty(Mono.fromCallable(() -> {
                            batch.setNextOffset(offset);
                            batch.setSourceExhausted(true);
                            return batch;
                        }
                ));
    }

    private Mono<CollectedBatch> processBatch(VacancySearchFilter filter,
                                              int limit,
                                              CollectedBatch batch,
                                              int batchCount,
                                              TrudVsemResponse response,
                                              int batchOffset,
                                              int toSkip) {
        var rawVacancies = response.getVacanciesSafe();
        var total = response.getMeta().getTotal();

        var found = batch.getMatched();
        var index = toSkip;
        for (int i = toSkip; i < rawVacancies.size(); i++) {
            var dto = rawVacancies.get(i);
            if (matchesSalary(dto, filter)) {
                found.add(mapper.toEntity(dto));
            }

            index = i + 1;
            if (found.size() == limit) {
                break;
            }
        }

        var nextOffset = batchOffset + index;
        var sourceExhausted = nextOffset >= total
                || rawVacancies.isEmpty()
                || toSkip >= rawVacancies.size();
        var haveEnough = found.size() >= limit;
        if (haveEnough || sourceExhausted) {
            batch.setNextOffset(nextOffset);
            batch.setSourceExhausted(sourceExhausted);
            batch.setTotal(total);
            return Mono.just(batch);
        }
        return collectMatching(filter, limit, nextOffset, batch, batchCount + 1);
    }

    private boolean matchesSalary(TrudVsemVacancyDto vacancy, VacancySearchFilter filter) {
        var vacancyMin = vacancy.getSalaryMin();
        var vacancyMax = vacancy.getSalaryMax();
        if (vacancyMin == null && vacancyMax == null) {
            return false;
        }

        var min = filter.getMinSalary();
        var max = filter.getMaxSalary();
        if (min != null) {
            var upper = vacancyMax != null ? vacancyMax : vacancyMin;
            if (upper < min) {
                return false;
            }
        }

        if (max != null) {
            var lower = vacancyMin != null ? vacancyMin : vacancyMax;
            return lower <= max;
        }
        return true;
    }

    private VacanciesResponse createResponse(CollectedBatch collected,
                                             VacancySearchFilter filter,
                                             int limit) {
        var matched = collected.getMatched();

        if (matched.isEmpty()) {
            var empty = createEmptyResponse();
            empty.setModifiedFrom(filter.getModifiedFrom());
            empty.setPage(collected.getNextPage());
            empty.setMore(!collected.isSourceExhausted());
            empty.setTotal(collected.getTotal());
            return empty;
        }

        var response = VacanciesResponse.builder()
                .source(SOURCE)
                .vacancies(matched)
                .more(matched.size() > limit || !collected.isSourceExhausted())
                .total(-1)
                .build();

        response.setModifiedFrom(filter.getModifiedFrom());
        return response;
    }


    private VacanciesResponse createResponse(TrudVsemResponse response, int limit, int page) {
        var vacancies = response.getVacanciesSafe()
                .stream()
                .map(mapper::toEntity)
                .toList();
        var result = new VacanciesResponse();
        var total = response.getMeta().getTotal();

        var isMore = (page + limit) < total;

        result.setVacancies(vacancies);
        result.setMore(isMore);
        result.setTotal(total);
        result.setSource(SOURCE);
        result.setPage(page);
        result.setCanHasOther(total > limit);

        return result;
    }

    private VacanciesResponse createEmptyResponse() {
        log.info("Не было найдено вакансий с TrudVsem");
        return VacanciesResponse.builder()
                .vacancies(List.of())
                .source(SOURCE)
                .build();
    }
}
