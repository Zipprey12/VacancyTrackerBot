package vacancy_tracker.services.telegram.settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vacancy_tracker.model.telegram.notification.NotificationSettings;
import vacancy_tracker.repository.NotificationSettingsRepository;
import vacancy_tracker.services.telegram.notification.NotificationQueueService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceImpl")
class NotificationServiceImplTest {

    final long sessionId = 1L;
    @Mock
    NotificationSettingsRepository repository;
    @Mock
    NotificationQueueService queueService;
    NotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NotificationServiceImpl(repository, queueService);
    }

    @Test
    @DisplayName("GetOrCreate should delegate to repository.getOrCreate")
    void delegatesToRepository() {
        var settings = new NotificationSettings();
        when(repository.getOrCreate(sessionId)).thenReturn(settings);

        var result = service.get(sessionId);

        assertThat(result).isSameAs(settings);
    }


    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Should schedule via queueService")
        void schedulesViaQueueService() {
            var settings = new NotificationSettings();
            when(repository.save(sessionId, settings)).thenReturn(settings);

            service.save(sessionId, settings);

            verify(queueService).schedule(sessionId, settings);
        }

        @Test
        @DisplayName("Should delegate to repository and return its result")
        void delegatesToRepository() {
            var settings = new NotificationSettings();
            var saved = new NotificationSettings();
            when(repository.save(sessionId, settings)).thenReturn(saved);

            var result = service.save(sessionId, settings);

            assertThat(result).isSameAs(saved);
        }

        @Test
        @DisplayName("Should clamp lastNotificationAt to nextNotificationAt when lastNotificationAt is after next")
        void clampsLastNotificationWhenAfterNext() {
            var next = LocalDateTime.now();
            var last = next.plusHours(1);
            var settings = new NotificationSettings();
            settings.setNextNotificationAt(next);
            settings.setLastNotificationAt(last);
            when(repository.save(sessionId, settings)).thenReturn(settings);

            service.save(sessionId, settings);

            assertThat(settings.getLastNotificationAt()).isEqualTo(next);
        }

        @Test
        @DisplayName("Should not change lastNotificationAt when it is before or equal to next")
        void doesNotChangeLastNotificationWhenValid() {
            var next = LocalDateTime.now();
            var last = next.minusHours(1);
            var settings = new NotificationSettings();
            settings.setNextNotificationAt(next);
            settings.setLastNotificationAt(last);
            when(repository.save(sessionId, settings)).thenReturn(settings);

            service.save(sessionId, settings);

            assertThat(settings.getLastNotificationAt()).isEqualTo(last);
        }

        @Test
        @DisplayName("Should not throw when lastNotificationAt or nextNotificationAt is null")
        void handlesNullDatesGracefully() {
            var settings = new NotificationSettings();
            settings.setNextNotificationAt(null);
            settings.setLastNotificationAt(null);
            when(repository.save(sessionId, settings)).thenReturn(settings);

            service.save(sessionId, settings);

            assertThat(settings.getLastNotificationAt()).isNull();
        }
    }

    @Nested
    @DisplayName("remove")
    class Remove {

        @Test
        @DisplayName("Should remove from repository and cancel in queue")
        void removesAndCancels() {
            service.remove(sessionId);

            verify(repository).remove(sessionId);
            verify(queueService).cancel(sessionId);
        }
    }

    @Nested
    @DisplayName("findEnabled")
    class FindEnabled {

        @Test
        @DisplayName("Should delegate to repository")
        void delegatesToRepository() {
            var expected = List.of(new NotificationSettings(), new NotificationSettings());
            when(repository.findEnabled()).thenReturn(expected);

            var result = service.findEnabled();

            assertThat(result).isEqualTo(expected);
        }
    }
}