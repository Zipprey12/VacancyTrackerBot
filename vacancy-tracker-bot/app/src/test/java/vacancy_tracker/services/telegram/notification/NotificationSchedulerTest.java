package vacancy_tracker.services.telegram.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationScheduler")
class NotificationSchedulerTest {

    private static final int MAX_BATCH_SIZE = 10;

    @Mock
    NotificationQueueService queueService;

    @Mock
    NotificationProcessor processor;

    NotificationScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new NotificationScheduler(queueService, processor);
    }


    @Test
    @DisplayName("Should not call processor when no overdue chats")
    void emptyListDoesNothing() {
        when(queueService.getOverdue(MAX_BATCH_SIZE)).thenReturn(List.of());

        scheduler.processNotifications();

        verifyNoInteractions(processor);
    }

    @Test
    @DisplayName("Should call processAsync for each overdue chatId")
    void processesEachOverdueChat() {
        var overdueChats = List.of(1L, 2L, 3L);
        when(queueService.getOverdue(MAX_BATCH_SIZE)).thenReturn(overdueChats);

        scheduler.processNotifications();

        verify(processor).processAsync(1L);
        verify(processor).processAsync(2L);
        verify(processor).processAsync(3L);
        verifyNoMoreInteractions(processor);
    }

    @Test
    @DisplayName("Should request correct batch size")
    void requestsCorrectBatchSize() {
        when(queueService.getOverdue(MAX_BATCH_SIZE)).thenReturn(List.of());

        scheduler.processNotifications();

        verify(queueService).getOverdue(MAX_BATCH_SIZE);
    }
}
