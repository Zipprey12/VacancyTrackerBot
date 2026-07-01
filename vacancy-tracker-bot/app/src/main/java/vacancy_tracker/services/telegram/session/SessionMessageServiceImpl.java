package vacancy_tracker.services.telegram.session;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionMessageServiceImpl implements SessionMessagesService {

    private static final String KEY_PREFIX = "last_message:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveLast(long chatId, int messageId) {
        redisTemplate.opsForValue().set(createKey(chatId), String.valueOf(messageId));
    }

    @Override
    public Optional<Integer> getLast(long chatId) {
        var value = redisTemplate.opsForValue().get(createKey(chatId));
        return value == null ? Optional.empty() : Optional.of(Integer.parseInt(value));
    }

    @Override
    public boolean isLast(long chatId, int messageId) {
        return getLast(chatId).map(id -> id == messageId).orElse(false);
    }

    private String createKey(long chatId) {
        return KEY_PREFIX + chatId;
    }
}
