package vacancy_tracker.services.telegram.pagination;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("VacanciesPaginationOffsetService")
class VacanciesPaginationOffsetServiceTest {

    private static final String KEY_PREFIX = "pagination:";
    final long chatId = 1;
    final int messageId = 100;
    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    HashOperations<String, Object, Object> hashOperations;
    VacanciesPaginationOffsetService service;

    @BeforeEach
    void setUp() {
        service = new VacanciesPaginationOffsetService(redisTemplate);
    }

    @Nested
    @DisplayName("resolveStartOffset")
    class ResolveStartOffset {

        @Test
        @DisplayName("Should return zero offset for page 0 without calling redis")
        void firstPageReturnZero() {
            var result = service.resolveStartOffset(chatId, messageId, 0);

            assertThat(result.getPage()).isZero();
            assertThat(result.getOffset()).isZero();
            verifyNoInteractions(redisTemplate);
        }

        @Test
        @DisplayName("Should return zero offset when messageId is null")
        void nullMessageIdReturnZero() {
            var result = service.resolveStartOffset(chatId, null, 3);

            assertThat(result.getPage()).isZero();
            assertThat(result.getOffset()).isZero();
        }

        @Test
        @DisplayName("Should return zero offset when no map exists for message")
        void noMapReturnZero() {
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(KEY_PREFIX + chatId, String.valueOf(messageId))).thenReturn(null);

            var result = service.resolveStartOffset(chatId, messageId, 3);

            assertThat(result.getPage()).isZero();
            assertThat(result.getOffset()).isZero();
        }

        @Test
        @DisplayName("Should return exact offset when page is present in stored map")
        void exactPageFound() throws Exception {
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            var json = new ObjectMapper().writeValueAsString(Map.of(2, 50));
            when(hashOperations.get(KEY_PREFIX + chatId, String.valueOf(messageId))).thenReturn(json);

            var result = service.resolveStartOffset(chatId, messageId, 2);

            assertThat(result.getPage()).isEqualTo(2);
            assertThat(result.getOffset()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should return zero when no previous page is stored either")
        void noPreviousPageReturnZero() throws Exception {
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            var json = new ObjectMapper().writeValueAsString(Map.of(5, 200));
            when(hashOperations.get(KEY_PREFIX + chatId, String.valueOf(messageId))).thenReturn(json);

            var result = service.resolveStartOffset(chatId, messageId, 2);

            assertThat(result.getPage()).isZero();
            assertThat(result.getOffset()).isZero();
        }
    }

    @Nested
    @DisplayName("saveNextPageOffset")
    class SaveNextPageOffset {

        @Test
        @DisplayName("Should put serialized map into redis hash")
        void putSerializedMap() {
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(KEY_PREFIX + chatId, String.valueOf(messageId))).thenReturn(null);
            when(hashOperations.keys(KEY_PREFIX + chatId)).thenReturn(Set.of(String.valueOf(messageId)));

            service.saveNextPageOffset(chatId, messageId, 1, 50L);

            verify(redisTemplate).expire(KEY_PREFIX + chatId, Duration.ofHours(1));
            verify(hashOperations).put(eq(KEY_PREFIX + chatId), eq(String.valueOf(messageId)), anyString());
        }

        @Test
        @DisplayName("Should remove oldest message when limit exceeded")
        void removeOldestWhenLimitExceeded() {
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(anyString(), anyString())).thenReturn(null);

            var manyFields = Set.<Object>of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
            when(hashOperations.keys(KEY_PREFIX + chatId)).thenReturn(manyFields);

            service.saveNextPageOffset(chatId, messageId, 1, 50L);

            verify(hashOperations).delete(KEY_PREFIX + chatId, "1");
        }

        @Test
        @DisplayName("Should not remove anything when under the limit")
        void doNotRemoveWhenUnderLimit() {
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(anyString(), anyString())).thenReturn(null);
            when(hashOperations.keys(KEY_PREFIX + chatId)).thenReturn(Set.of("1", "2"));

            service.saveNextPageOffset(chatId, messageId, 1, 50L);

            verify(hashOperations, never()).delete(anyString(), any());
        }

        @Test
        @DisplayName("Should not throw when operation fail")
        void doNotThrowOnError() {
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(anyString(), anyString())).thenThrow(new RuntimeException("redis error"));

            assertThatCode(() -> {
                service.saveNextPageOffset(chatId, messageId, 1, 50L);
            }).doesNotThrowAnyException();
        }
    }
}