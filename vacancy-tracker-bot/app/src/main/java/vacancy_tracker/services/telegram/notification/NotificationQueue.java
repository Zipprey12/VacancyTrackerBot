package vacancy_tracker.services.telegram.notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationQueue {

    void add(long chatId, LocalDateTime executionTime);

    void remove(long chatId);

    List<Long> dequeueEarlierThan(LocalDateTime dateTime, int maxCount);

    void clear();
}
