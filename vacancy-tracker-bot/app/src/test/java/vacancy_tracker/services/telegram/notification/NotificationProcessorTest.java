package vacancy_tracker.services.telegram.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vacancy_tracker.model.telegram.execution.ExecutionFailReason;
import vacancy_tracker.model.telegram.execution.ExecutionResult;
import vacancy_tracker.model.telegram.notification.NotificationSettings;
import vacancy_tracker.services.telegram.actions.vacancies.SendVacanciesAction;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.settings.NotificationService;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationProcessor")
class NotificationProcessorTest {

    final long chatId = 1L;
    @Mock
    NotificationService notificationService;
    @Mock
    SendVacanciesAction showVacanciesAction;
    NotificationProcessor processor;
    SequentialAsyncExecutionStrategy strategy = initStrategy();

    @BeforeEach
    void setUp() {
        processor = new NotificationProcessor(notificationService, showVacanciesAction, strategy);
    }

    private SequentialAsyncExecutionStrategy initStrategy() {
        return new SequentialAsyncExecutionStrategy(Executors.newFixedThreadPool(4));
    }

    private NotificationSettings enabledSettings() {
        var settings = new NotificationSettings();
        settings.setChatId(chatId);
        settings.setEnabled(true);
        settings.setInterval(Duration.ofHours(1));
        settings.setUpdatedAt(Instant.now().minusSeconds(60));
        return settings;
    }

    private NotificationSettings mockEnabledSettings() {
        var settings = mock(NotificationSettings.class);
        when(settings.isEnabled()).thenReturn(true);
        return settings;
    }

    private NotificationSettings updatedSettingsAfterNow() {
        var settings = enabledSettings();
        settings.setNextNotificationAt(LocalDateTime.now());
        return settings;
    }

    @Nested
    @DisplayName("processAsync skip conditions")
    class SkipConditions {

        @Test
        @DisplayName("Should not call action when settings = null")
        void nullSettingsSkip() {
            when(notificationService.get(chatId)).thenReturn(null);

            processor.processAsync(chatId);

            verifyNoInteractions(showVacanciesAction);
        }

        @Test
        @DisplayName("Should not call action when disabled")
        void disabledSkip() {
            var settings = enabledSettings();
            settings.setEnabled(false);
            when(notificationService.get(chatId)).thenReturn(settings);

            processor.processAsync(chatId);

            verifyNoInteractions(showVacanciesAction);
        }
    }

    @Nested
    @DisplayName("processAsync - result processing")
    class ResultProcessing {

        @Test
        @DisplayName("Should not change notification settings when exception")
        void exceptionSkip() {
            var result = new ExecutionResult(false, ExecutionFailReason.EXCEPTION);
            var settings = enabledSettings();
            var nextNotificationAt = settings.getNextNotificationAt();

            when(notificationService.get(chatId)).thenReturn(settings);
            when(showVacanciesAction.executeWithCompletionCheck(any(), any()))
                    .thenReturn(CompletableFuture.completedFuture(result));

            processor.processAsync(chatId);

            verify(notificationService, never()).save(anyLong(), any());
            assertThat(settings.getNextNotificationAt()).isEqualTo(nextNotificationAt);
        }

        @Test
        @DisplayName("Should scheduleNext when result is success")
        void updateNextAndLastNotificationAtWhenMessageSend() {
            var result = ExecutionResult.success();
            var settings = mockEnabledSettings();

            when(notificationService.get(chatId)).thenReturn(settings);
            when(showVacanciesAction.executeWithCompletionCheck(any(), any()))
                    .thenReturn(CompletableFuture.completedFuture(result));

            processor.processAsync(chatId);

            await().atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(settings).scheduleNext();
                        verify(settings, never()).rescheduleNext();
                    });
        }

        @Test
        @DisplayName("Should rescheduleNext when result is not success and reason is not exception")
        void rescheduleNextWhenEmptyResult() {
            var result = ExecutionResult.fail(ExecutionFailReason.EMPTY_RESULT);
            var settings = mockEnabledSettings();

            when(notificationService.get(chatId)).thenReturn(settings);
            when(showVacanciesAction.executeWithCompletionCheck(any(), any()))
                    .thenReturn(CompletableFuture.completedFuture(result));

            processor.processAsync(chatId);

            await().atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(settings).rescheduleNext();
                        verify(settings, never()).scheduleNext();
                    });
        }

        @Test
        @DisplayName("Should not update settings if they have been updated during execution")
        void settingsUpdatedAfterStartSkipsSave() {
            var initialSettings = enabledSettings();

            when(notificationService.get(chatId))
                    .thenReturn(initialSettings)
                    .thenReturn(updatedSettingsAfterNow());

            when(showVacanciesAction.executeWithCompletionCheck(any(), any()))
                    .thenReturn(CompletableFuture.completedFuture(ExecutionResult.success()));

            processor.processAsync(chatId);

            await().atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(notificationService, never()).save(anyLong(), any());
                    });
        }
    }
}