package vacancy_tracker.repository;

import vacancy_tracker.model.telegram.UserSession;

import java.util.Optional;

public interface SessionsRepository {

    Optional<UserSession> register(long chatId);

    Optional<UserSession> findById(long chatId);

}
