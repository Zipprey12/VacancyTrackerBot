package vacancy_tracker.services.telegram.session;

import vacancy_tracker.model.telegram.session.UserSessionContext;

public interface SessionsService {

    UserSessionContext getOrCreateSession(long chatId);

    UserSessionContext addSession(long chatId);

    UserSessionContext save(UserSessionContext session);

    void disableInterceptor(long chatId);

    void enableInterceptor(long chatId, String key);
}
