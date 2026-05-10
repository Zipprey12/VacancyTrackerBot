package vacancy_tracker.repository;

import vacancy_tracker.model.telegram.UserSessionContext;

import java.util.Optional;

public interface SessionsRepository {

    Optional<UserSessionContext> register(long chatId);

    Optional<UserSessionContext> findById(long chatId);

    void save(UserSessionContext sessionContext);
}
