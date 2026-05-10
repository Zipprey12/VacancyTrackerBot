package vacancy_tracker.services.telegram.session;

import vacancy_tracker.model.telegram.UserSessionContext;

public interface SessionsService {

    UserSessionContext getSession(long chatId);

    boolean hasSession(long chatId);

    void addSession(long chatId);

    void save(UserSessionContext session);
}
