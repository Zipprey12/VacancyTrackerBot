package vacancy_tracker.services.telegram.session;

import java.util.Optional;

public interface SessionMessagesService {

    void saveLast(long chatId, int messageId);

    Optional<Integer> getLast(long chatId);

    boolean isLast(long chatId, int messageId);
}
