package vacancy_tracker.services.telegram.pagination;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.search.PaginationArgs;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacanciesPaginationOffsetService {

    private static final String KEY_PREFIX = "pagination:";
    private static final int MAX_MESSAGES_PER_CHAT = 10;
    private static final Duration TTL = Duration.ofHours(1);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaginationArgs resolveStartOffset(long chatId, Integer messageId, int page) {
        if (page == 0 || messageId == null) {
            return new PaginationArgs(0, 0);
        }

        var map = readMap(chatId, messageId);
        if (map.isEmpty()) {
            log.debug("Карта пагинации не найдена для chatId={}, messageId={}", chatId, messageId);
            return new PaginationArgs(0, 0);
        }

        var offset = map.get(page);
        if (offset != null) {
            return new PaginationArgs(page, offset);
        }

        var closestPrevious = map.entrySet().stream()
                .filter(e -> e.getKey() < page)
                .max(Comparator.comparingInt(Map.Entry::getKey));

        if (closestPrevious.isPresent()) {
            var foundPage = closestPrevious.get().getKey();
            var foundOffset = closestPrevious.get().getKey();
            log.debug("Offset для страницы {} не найден. Используется offset страницы {}",
                    page, foundPage);
            return new PaginationArgs(foundPage, foundOffset);
        }
        return new PaginationArgs(0, 0);
    }

    public void saveNextPageOffset(long chatId, int messageId, int nextPage, long nextOffset) {
        var key = buildKey(chatId);
        var ops = redisTemplate.opsForHash();

        var map = readMap(chatId, messageId);
        if (map.isEmpty()) {
            map = new LinkedHashMap<>();
        }
        map.put(nextPage, (int) nextOffset);

        try {
            var json = objectMapper.writeValueAsString(map);
            ops.put(key, String.valueOf(messageId), json);
            redisTemplate.expire(key, TTL);
            adjustMessageLimit(chatId);
            log.debug("Сохранён offset {} для страницы {} (chatId={}, messageId={})",
                    nextOffset, nextPage, chatId, messageId);
        } catch (Exception e) {
            log.warn("Не удалось сохранить offset пагинации chatId={}, messageId={}", chatId, messageId, e);
        }
    }

    private Map<Integer, Integer> readMap(long chatId, int messageId) {
        var key = buildKey(chatId);
        var ops = redisTemplate.opsForHash();
        var json = (String) ops.get(key, String.valueOf(messageId));

        if (json == null) {
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<TreeMap<Integer, Integer>>() {
            });
        } catch (Exception e) {
            log.warn("Не удалось прочитать карту пагинации chatId={}, messageId={}", chatId, messageId, e);
            return Collections.emptyMap();
        }
    }

    private void adjustMessageLimit(long chatId) {
        var key = buildKey(chatId);
        var ops = redisTemplate.opsForHash();
        var fields = ops.keys(key);

        if (fields.size() <= MAX_MESSAGES_PER_CHAT) {
            return;
        }

        var oldest = fields.stream()
                .map(f -> (String) f)
                .min(Comparator.comparingInt(Integer::parseInt));

        oldest.ifPresent(field -> {
            ops.delete(key, field);
            log.debug("Удалена устаревшая карта пагинации chatId={}, messageId={}", chatId, field);
        });
    }

    private String buildKey(long chatId) {
        return KEY_PREFIX + chatId;
    }
}
