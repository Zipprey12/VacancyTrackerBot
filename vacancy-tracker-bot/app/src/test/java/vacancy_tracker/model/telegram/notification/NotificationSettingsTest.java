package vacancy_tracker.model.telegram.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("NotificationSettings")
class NotificationSettingsTest {

    @Nested
    @DisplayName("scheduleNext")
    class ScheduleNext {

        @Test
        @DisplayName("Should do nothing when interval is null")
        void nullIntervalDoesNothing() {
            var settings = new NotificationSettings();
            settings.setInterval(null);

            settings.scheduleNext();

            assertThat(settings.getNextNotificationAt()).isNull();
        }

        @Test
        @DisplayName("Should set nextNotificationAt to now + interval on first call")
        void firstSchedule() {
            var settings = new NotificationSettings();
            settings.setInterval(Duration.ofHours(1));
            var before = LocalDateTime.now();

            settings.scheduleNext();

            assertThat(settings.getNextNotificationAt())
                    .isAfterOrEqualTo(before.plusHours(1))
                    .isBefore(before.plusHours(1).plusSeconds(5));
        }

        @Test
        @DisplayName("Should move nextNotificationAt to lastNotificationAt and advance by interval")
        void subsequentSchedule() {
            var settings = new NotificationSettings();
            settings.setInterval(Duration.ofDays(1));
            var first = LocalDateTime.now().plusDays(1);
            settings.setNextNotificationAt(first);

            settings.scheduleNext();

            assertThat(settings.getLastNotificationAt()).isEqualTo(first);
            assertThat(settings.getNextNotificationAt())
                    .isCloseTo(first.plusDays(1), within(1, ChronoUnit.SECONDS));
        }

        @Test
        @DisplayName("Should jump to now + interval when behind schedule")
        void skipsToNowPlusIntervalWhenBehind() {
            var settings = new NotificationSettings();
            settings.setInterval(Duration.ofMinutes(5));
            settings.setNextNotificationAt(LocalDateTime.now().minusDays(10));

            settings.scheduleNext();

            assertThat(settings.getNextNotificationAt()).isAfter(LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("rescheduleNext")
    class RescheduleNext {

        @Test
        @DisplayName("Should do nothing when interval is null")
        void nullIntervalDoesNothing() {
            var settings = new NotificationSettings();
            settings.setInterval(null);

            settings.rescheduleNext();

            assertThat(settings.getNextNotificationAt()).isNull();
        }

        @Test
        @DisplayName("Should set nextNotificationAt to now + interval on first call")
        void firstReschedule() {
            var settings = new NotificationSettings();
            settings.setInterval(Duration.ofHours(2));
            var before = LocalDateTime.now();

            settings.rescheduleNext();

            assertThat(settings.getNextNotificationAt())
                    .isAfterOrEqualTo(before.plusHours(2))
                    .isBefore(before.plusHours(2).plusSeconds(5));
        }

        @Test
        @DisplayName("Should not update lastNotificationAt, unlike scheduleNext")
        void doesNotUpdateLastNotification() {
            var settings = new NotificationSettings();
            settings.setInterval(Duration.ofDays(1));
            var first = LocalDateTime.now().plusDays(1);
            settings.setNextNotificationAt(first);
            settings.setLastNotificationAt(null);

            settings.rescheduleNext();

            assertThat(settings.getLastNotificationAt()).isNull();
            assertThat(settings.getNextNotificationAt())
                    .isCloseTo(first.plusDays(1), within(1, ChronoUnit.SECONDS));
        }

        @Test
        @DisplayName("Should jump to now + interval when behind schedule")
        void skipsToNowPlusIntervalWhenBehind() {
            var settings = new NotificationSettings();
            settings.setInterval(Duration.ofMinutes(5));
            settings.setNextNotificationAt(LocalDateTime.now().minusDays(10));

            settings.rescheduleNext();

            assertThat(settings.getNextNotificationAt()).isAfter(LocalDateTime.now());
        }
    }
}