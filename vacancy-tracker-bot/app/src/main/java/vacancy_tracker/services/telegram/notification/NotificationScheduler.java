package vacancy_tracker.services.telegram.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final int MAX_BATCH_SIZE = 10;

    private final NotificationQueueService queueService;
    private final NotificationProcessor processor;

    @Scheduled(fixedDelay = 1000)
    public void processNotifications() {
        var dueChats = queueService.getOverdue(MAX_BATCH_SIZE);
        if (dueChats.isEmpty()) {
            return;
        }

        log.debug("Обработка {} уведомлений", dueChats.size());
        dueChats.forEach(processor::processAsync);
    }
}
