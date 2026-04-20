package vacancy_tracker.services.telegram.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vacancy_tracker.model.telegram.UserSession;
import vacancy_tracker.repository.in_memory.SimpleInMemorySessionsRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleSessionsServiceImpl implements SessionsService {

    private final SimpleInMemorySessionsRepository repository;

    @Override
    public UserSession getSession(long chatId) {
        var existed = repository.findById(chatId);
        if(existed.isPresent()){
            return existed.get();
        }

        var registered = repository.register(chatId);
        if(registered.isPresent()){
            return registered.get();
        }

        log.error("Не удалось зарегистрировать сессию: {}", chatId);
        throw new RuntimeException("Не удалось сохранить данные сессии");
    }

    @Override
    public boolean hasSession(long chatId) {
        return repository.findById(chatId).isPresent();
    }

    @Override
    public void addSession(long chatId) {
        repository.register(chatId);
    }
}
