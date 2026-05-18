package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.telegram.UserSessionContext;
import vacancy_tracker.repository.SessionsRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySessionsRepository implements SessionsRepository {

    private final Map<Long, UserSessionContext> sessions = new ConcurrentHashMap<>();

    @Override
    public Optional<UserSessionContext> register(long chatId) {
        var session = new UserSessionContext(chatId);
        sessions.put(chatId, session);
        return Optional.of(session);
    }

    @Override
    public Optional<UserSessionContext> findById(long chatId) {
        return Optional.ofNullable(sessions.get(chatId));
    }

    @Override
    public void save(UserSessionContext sessionContext) {
        sessions.put(sessionContext.getChatId(), sessionContext);
    }
}
