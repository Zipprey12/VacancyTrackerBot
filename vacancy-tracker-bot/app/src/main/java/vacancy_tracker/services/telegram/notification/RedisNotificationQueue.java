package vacancy_tracker.services.telegram.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisNotificationQueue implements NotificationQueue {

    private static final String QUEUE_KEY = "notification:queue";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void add(long chatId, LocalDateTime executionTime) {
        var ops = redisTemplate.opsForZSet();
        var member = String.valueOf(chatId);
        ops.remove(QUEUE_KEY, member);
        ops.add(QUEUE_KEY, member, toScore(executionTime));
        log.debug("Задача добавлена в очередь: chatId={}, executionTime={}", chatId, executionTime);
    }

    @Override
    public void remove(long chatId) {
        var ops = redisTemplate.opsForZSet();
        ops.remove(QUEUE_KEY, String.valueOf(chatId));
        log.debug("Задача удалена из очереди: chatId={}", chatId);
    }

    @Override
    public List<Long> dequeueEarlierThan(LocalDateTime dateTime, int maxCount) {
        var ops = redisTemplate.opsForZSet();
        long maxScore = toScore(dateTime);

        var members = ops.rangeByScore(QUEUE_KEY, 0, maxScore, 0, maxCount);
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        var array = members.toArray();
        ops.remove(QUEUE_KEY, array);
        log.debug("Получены задачи на отправку вакансий для чатов: {}", array);

        return members.stream()
                .map(Long::parseLong)
                .toList();
    }

    @Override
    public void clear() {
        redisTemplate.delete(QUEUE_KEY);
        log.info("Очередь нотификации очищена");
    }

    private long toScore(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }
}
