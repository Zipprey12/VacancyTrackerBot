package vacancy_tracker.repository.in_memory;

import org.springframework.stereotype.Repository;
import vacancy_tracker.model.telegram.UserSessionContext;
import vacancy_tracker.repository.SessionsRepository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class SimpleInMemorySessionsRepository implements SessionsRepository {

    private final HashMap<Long, UserSessionContext> sessions = new HashMap<>();

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
}
