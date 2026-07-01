package vacancy_tracker.services.telegram.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.session.UserSessionContext;
import vacancy_tracker.repository.SessionsRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionsServiceImpl implements SessionsService {

    private final SessionsRepository repository;
    private final ApplicationContext context;

    private SessionsService self() {
        return context.getBean(SessionsService.class);
    }

    @Override
    @Caching(
            cacheable = @Cacheable(value = "sessions", key = "#chatId", unless = "#result == null"),
            evict = @CacheEvict(value = "sessions", key = "'has_' + #chatId")
    )
    public UserSessionContext getOrCreateSession(long chatId) {
        var existed = repository.findById(chatId);
        if (existed.isPresent()) {
            var session = existed.get();
            session.setNew(false);
            return existed.get();
        }

        var registered = repository.register(chatId);
        if (registered.isPresent()) {
            return registered.get();
        }

        log.error("Не удалось зарегистрировать сессию: {}", chatId);
        throw new IllegalStateException("Не удалось сохранить данные сессии");
    }

    @Override
    @Caching(
            put = @CachePut(value = "sessions", key = "#result.chatId"),
            evict = @CacheEvict(value = "sessions", key = "'has_' + #chatId")
    )
    public UserSessionContext addSession(long chatId) {
        return repository.register(chatId)
                .orElseThrow(() -> new IllegalStateException("Не удалось сохранить данные сессии"));
    }

    @Override
    @CachePut(value = "sessions", key = "#session.chatId")
    public UserSessionContext save(UserSessionContext session) {
        repository.save(session);
        return session;
    }

    @Override
    public void disableInterceptor(long chatId) {
        var session = self().getOrCreateSession(chatId);
        if (session != null) {
            session.deleteInterceptor();
            self().save(session);
            log.debug("Отключен перехватчик ввода для {}", chatId);
        }
    }

    @Override
    public void enableInterceptor(long chatId, String key) {
        var session = self().getOrCreateSession(chatId);
        session.setInputHandlerKey(key);
        self().save(session);
    }
}