package vacancy_tracker.services.telegram.settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.repository.SearchFiltersRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchFiltersServiceImpl")
class SearchFiltersServiceImplTest {

    final long sessionId = 1L;
    @Mock
    SearchFiltersRepository repository;
    SearchFiltersServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SearchFiltersServiceImpl(repository);
    }

    @Nested
    @DisplayName("get")
    class Get {

        @Test
        @DisplayName("Should return existing filter from repository")
        void returnExistingFilter() {
            var filter = new VacancySearchFilter();
            when(repository.get(sessionId)).thenReturn(Optional.of(filter));

            var result = service.get(sessionId);

            assertThat(result).isSameAs(filter);
            verify(repository, never()).save(anyLong(), any());
        }

        @Test
        @DisplayName("Should create and save new filter when not exists")
        void createNewFilterWhenAbsent() {
            when(repository.get(sessionId)).thenReturn(Optional.empty());

            var result = service.get(sessionId);

            assertThat(result).isNotNull();
            verify(repository).save(sessionId, result);
        }

        @Test
        @DisplayName("Should delegate to repository and return its result")
        void savaDelegateToRepository() {
            var filter = new VacancySearchFilter();
            var saved = new VacancySearchFilter();
            when(repository.save(sessionId, filter)).thenReturn(saved);

            var result = service.save(sessionId, filter);

            assertThat(result).isSameAs(saved);
            verify(repository).save(sessionId, filter);
        }

        @Test
        @DisplayName("Should delegate to repository.remove")
        void deleteDelegateToRepository() {
            service.delete(sessionId);

            verify(repository).remove(sessionId);
        }
    }
}