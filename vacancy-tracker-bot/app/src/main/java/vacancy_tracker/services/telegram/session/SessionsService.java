package vacancy_tracker.services.telegram.session;

import vacancy_tracker.model.telegram.UserSession;

public interface SessionsService {

    UserSession getSession(long chatId);

    boolean hasSession(long chatId);

    void addSession(long chatId);
}
