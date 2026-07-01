package vacancy_tracker.repository.jpa;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import vacancy_tracker.model.persistence.SessionEntity;
import vacancy_tracker.model.persistence.UserEntity;
import vacancy_tracker.model.telegram.session.UserSessionContext;
import vacancy_tracker.repository.SessionsRepository;
import vacancy_tracker.repository.jpa.dao.SessionDao;
import vacancy_tracker.repository.jpa.dao.UserDao;
import vacancy_tracker.services.mappers.SessionMapper;

import java.util.Optional;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class JpaSessionsRepository implements SessionsRepository {

    private final SessionDao sessionDao;
    private final UserDao userDao;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional
    public Optional<UserSessionContext> register(long chatId) {
        userDao.save(new UserEntity(chatId));
        sessionDao.save(new SessionEntity(chatId));
        return Optional.of(new UserSessionContext(chatId));
    }

    @Override
    public Optional<UserSessionContext> findById(long chatId) {
        return sessionDao.findById(chatId)
                .map(sessionMapper::toDomain);
    }

    @Override
    public void save(UserSessionContext session) {
        var entity = sessionMapper.toEntity(session);
        sessionDao.save(entity);
    }
}