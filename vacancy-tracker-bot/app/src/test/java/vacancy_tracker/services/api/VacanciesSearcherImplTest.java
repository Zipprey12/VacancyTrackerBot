package vacancy_tracker.services.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vacancy_tracker.model.domain.RequestType;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.search.VacanciesSearchParams;
import vacancy_tracker.model.search.VacancySearchFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static vacancy_tracker.model.domain.VacanciesSource.SUPER_JOB;
import static vacancy_tracker.model.domain.VacanciesSource.TRUD_VSEM;

@ExtendWith(MockitoExtension.class)
@DisplayName("VacanciesSearcherImpl")
class VacanciesSearcherImplTest {

    @Mock
    AsyncVacanciesProvider trudVsemProvider;

    @Mock
    AsyncVacanciesProvider superJobProvider;

    VacanciesSearcherImpl searcher;

    VacanciesSearchParams params;


    @BeforeEach
    void setUp() {
        when(trudVsemProvider.getSource()).thenReturn(TRUD_VSEM);
        when(superJobProvider.getSource()).thenReturn(SUPER_JOB);

        searcher = new VacanciesSearcherImpl(List.of(trudVsemProvider, superJobProvider));

        var filter = new VacancySearchFilter();
        filter.setRequestType(RequestType.MANUAL);

        params = VacanciesSearchParams.builder()
                .chatId(1L)
                .filter(filter)
                .limit(10)
                .page(0)
                .build();
    }

    @Nested
    @DisplayName("search(params)")
    class SearchAll {

        @Test
        @DisplayName("Should call find on all providers")
        void callAllProviders() {
            when(trudVsemProvider.find(params)).thenReturn(responseWith(TRUD_VSEM, 5));
            when(superJobProvider.find(params)).thenReturn(responseWith(SUPER_JOB, 3));

            var result = searcher.search(params).join();

            verify(superJobProvider).find(params);
            verify(trudVsemProvider).find(params);

            assertThat(result.getVacanciesResponses()).hasSize(2);
            assertThat(result.getVacanciesResultCount()).isEqualTo(8);
            assertThat(result.getRequestType()).isEqualTo(RequestType.MANUAL);
        }

        @Test
        @DisplayName("Should extend modified from to all responses")
        void extendModifiedFrom() {
            var modifiedFrom = LocalDateTime.of(2026, 1, 5, 12, 30);
            params.getFilter().setModifiedFrom(modifiedFrom);
            when(trudVsemProvider.find(params)).thenReturn(responseWith(TRUD_VSEM, 5));
            when(superJobProvider.find(params)).thenReturn(responseWith(SUPER_JOB, 3));

            var result = searcher.search(params).join();

            assertThat(result.getModifiedFrom()).isEqualTo(modifiedFrom);
            result.getVacanciesResponses().forEach(r ->
                    assertThat(r.getModifiedFrom()).isEqualTo(modifiedFrom));
        }
    }

    @Nested
    @DisplayName("search(params, source)")
    class SearchBySource {

        @Test
        @DisplayName("Should call only SuperJob when source is SUPER_JOB")
        void callOnlySuperJob() {
            when(superJobProvider.find(params)).thenReturn(responseWith(SUPER_JOB, 2));

            var result = searcher.search(params, SUPER_JOB).join();

            verify(superJobProvider).find(params);
            verify(trudVsemProvider, never()).find(any());
            assertThat(result.getVacanciesResponses()).hasSize(1);
        }

        @Test
        @DisplayName("Should call all providers when source is null")
        void nullSourceCallAll() {
            when(superJobProvider.find(params)).thenReturn(emptyResponse(SUPER_JOB));
            when(trudVsemProvider.find(params)).thenReturn(emptyResponse(TRUD_VSEM));

            searcher.search(params, null).join();

            verify(superJobProvider).find(params);
            verify(trudVsemProvider).find(params);
        }
    }

    @Nested
    @DisplayName("searchWithOutcome(params, source)")
    class SearchWithOutcome {

        @Test
        @DisplayName("Should return SearchOutcome with provider's onPublished callback")
        void returnCallbackFromProvider() {
            var response = responseWith(TRUD_VSEM, 3).join();
            when(trudVsemProvider.find(params)).thenReturn(CompletableFuture.completedFuture(response));

            var callbackCalled = new AtomicBoolean(false);
            when(trudVsemProvider.onPublished(params.getChatId(), response))
                    .thenReturn((id, page) -> callbackCalled.set(true));

            var outcome = searcher.searchWithOutcome(params, TRUD_VSEM).join();
            outcome.onPublished().accept(1, 1);

            assertThat(callbackCalled.get()).isTrue();
        }

        @Test
        @DisplayName("Should return withoutCallback when source is null and no non-empty responses")
        void nullSourceNoResponseReturnsNoopCallback() {
            when(superJobProvider.find(params)).thenReturn(emptyResponse(SUPER_JOB));
            when(trudVsemProvider.find(params)).thenReturn(emptyResponse(TRUD_VSEM));

            var outcome = searcher.searchWithOutcome(params, null).join();

            outcome.onPublished().accept(1, 2);
            verify(superJobProvider, never()).onPublished(anyLong(), any());
            verify(trudVsemProvider, never()).onPublished(anyLong(), any());
        }

        @Test
        @DisplayName("Should use first non-empty provider's callback when source is null")
        void nullSourceUseFirstNonEmptyProviderCallback() {
            var response = responseWith(SUPER_JOB, 2).join();
            when(superJobProvider.find(params)).thenReturn(CompletableFuture.completedFuture(response));
            when(trudVsemProvider.find(params)).thenReturn(emptyResponse(TRUD_VSEM));

            var callbackCalled = new AtomicBoolean(false);
            when(superJobProvider.onPublished(params.getChatId(), response))
                    .thenReturn((id, page) -> callbackCalled.set(true));

            var outcome = searcher.searchWithOutcome(params, null).join();
            outcome.onPublished().accept(1, 1);

            assertThat(callbackCalled.get()).isTrue();
        }
    }

    @Nested
    @DisplayName("makeTrialRequest")
    class MakeTrialRequest {

        @Test
        @DisplayName("Should set limit=1 and page=0 before calling providers")
        void setTrialLimits() {
            when(superJobProvider.makeTrialResponse(any())).thenReturn(emptyResponse(SUPER_JOB));
            when(trudVsemProvider.makeTrialResponse(any())).thenReturn(emptyResponse(TRUD_VSEM));

            params.setLimit(10);
            params.setPage(5);

            searcher.makeTrialRequest(params).join();

            assertThat(params.getLimit()).isEqualTo(1);
            assertThat(params.getPage()).isZero();

            verify(superJobProvider).makeTrialResponse(params);
            verify(trudVsemProvider).makeTrialResponse(params);
            verify(superJobProvider, never()).find(any());
            verify(trudVsemProvider, never()).find(any());
        }
    }

    private CompletableFuture<VacanciesResponse> emptyResponse(VacanciesSource source) {
        var response = VacanciesResponse.builder()
                .source(source)
                .vacancies(List.of())
                .total(0)
                .build();
        return CompletableFuture.completedFuture(response);
    }

    private CompletableFuture<VacanciesResponse> responseWith(VacanciesSource source, int count) {
        var vacancies = java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> new vacancy_tracker.model.domain.Vacancy())
                .toList();
        var response = VacanciesResponse.builder()
                .source(source)
                .vacancies(vacancies)
                .total(count)
                .build();
        return CompletableFuture.completedFuture(response);
    }

}