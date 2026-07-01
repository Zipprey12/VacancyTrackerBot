package vacancy_tracker.services.telegram.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vacancy_tracker.model.telegram.notification.NotificationSettings;
import vacancy_tracker.services.telegram.settings.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationQueueService")
class NotificationQueueServiceTest {

    final long chatId = 1;
    @Mock
    NotificationQueue queue;
    @Mock
    NotificationService notificationService;
    NotificationQueueService queueService;

    @BeforeEach
    void setUp() {
        queueService = new NotificationQueueService(queue, notificationService);
    }

    @Test
    @DisplayName("Cancel should remove chatId from queue")
    void removesFromQueue() {
        queueService.cancel(chatId);

        verify(queue).remove(chatId);
    }

    @Test
    @DisplayName("GetOverdue should delegate to queue.dequeueEarlierThan")
    void getOverdueDelegateToQueue() {
        var expected = List.of(1L, 2L, 3L);
        when(queue.dequeueEarlierThan(any(), eq(10))).thenReturn(expected);

        var result = queueService.getOverdue(10);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Clear should delegate to queue.clear")
    void clearDelegateToQueue() {
        queueService.clear();

        verify(queue).clear();
    }

    private NotificationSettings createSettings(long chatId, LocalDateTime nextNotificationAt) {
        var settings = new NotificationSettings();
        settings.setChatId(chatId);
        settings.setEnabled(true);
        settings.setNextNotificationAt(nextNotificationAt);
        return settings;
    }

    @Nested
    @DisplayName("initialize")
    class Initialize {

        @Test
        @DisplayName("Should clear queue before filling")
        void clearQueueFirst() {
            when(notificationService.findEnabled()).thenReturn(List.of());
            queueService.initialize();

            verify(queue).clear();
        }

        @Test
        @DisplayName("Should add all valid settings to queue")
        void addAllValidSettings() {
            var time1 = LocalDateTime.now().plusHours(1);
            var time2 = LocalDateTime.now().plusHours(2);
            var settings1 = createSettings(1L, time1);
            var settings2 = createSettings(2L, time2);
            when(notificationService.findEnabled()).thenReturn(List.of(settings1, settings2));

            queueService.initialize();

            verify(queue).add(1L, time1);
            verify(queue).add(2L, time2);
        }

        @ParameterizedTest(name = "chatId={0}, nextNotificationAt present={1}")
        @DisplayName("Should skip invalid settings")
        @CsvSource({
                "0, true",
                "1, false"
        })
        void skipInvalidSettings(long chatId, boolean hasNextNotification) {
            var nextTime = hasNextNotification ? LocalDateTime.now().plusHours(1) : null;
            var settings = createSettings(chatId, nextTime);
            when(notificationService.findEnabled()).thenReturn(List.of(settings));

            queueService.initialize();

            verify(queue, never()).add(anyLong(), any());
        }

        @Test
        @DisplayName("Should not throw when initialization fails")
        void exceptionIsCaught() {
            when(notificationService.findEnabled()).thenThrow(new RuntimeException("ошибка БД"));

            queueService.initialize();

            verify(queue).clear();
        }
    }

    @Nested
    @DisplayName("schedule")
    class Schedule {

        @Test
        @DisplayName("Should add to queue when enabled and has nextNotificationAt")
        void addsToQueueWhenEnabled() {
            var nextTime = LocalDateTime.now().plusHours(1);
            var settings = createSettings(chatId, nextTime);
            settings.setEnabled(true);

            queueService.schedule(chatId, settings);

            verify(queue).add(chatId, nextTime);
            verify(queue, never()).remove(anyLong());
        }

        @ParameterizedTest(name = "enabled = {0}, nextNotificationAt present = {1}")
        @DisplayName("Should remove from queue when disabled or nextNotificationAt is null")
        @CsvSource({
                "false, true",
                "true, false"
        })
        void removesFromQueueWhenDisabled(boolean isEnabled, boolean nextNotificationPresent) {
            var date = nextNotificationPresent ? LocalDateTime.now().plusHours(1) : null;
            var settings = createSettings(chatId, date);
            settings.setEnabled(isEnabled);

            queueService.schedule(chatId, settings);

            verify(queue).remove(chatId);
            verify(queue, never()).add(anyLong(), any());
        }
    }
}