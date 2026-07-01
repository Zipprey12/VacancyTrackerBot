package vacancy_tracker.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.session.UserSessionContext;
import vacancy_tracker.services.telegram.callback.CallbackService;
import vacancy_tracker.services.telegram.navigation.BotNavigator;
import vacancy_tracker.services.telegram.session.SessionsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VacancyTrackerBot")
class VacancyTrackerBotTest {

    @Mock
    private BotNavigator navigator;

    @Mock
    private SessionsService sessionsService;

    @Mock
    private CallbackService callbackService;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private CallbackQuery callbackQuery;

    @InjectMocks
    private VacancyTrackerBot bot;

    private static final long CHAT_ID = 12345;
    private static final String BOT_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        bot = new VacancyTrackerBot(BOT_TOKEN, navigator, sessionsService, callbackService);
    }

    @Nested
    @DisplayName("consume")
    class Consume {

        @Test
        @DisplayName("Should process update without throwing exception")
        void shouldProcessUpdate() {
            mockMessageUpdate();
            mockExistingSession();

            bot.consume(update);

            verify(navigator).navigate(update);
            verify(sessionsService).getOrCreateSession(CHAT_ID);
        }

        @Test
        @DisplayName("Should handle callback query")
        void shouldHandleCallback() {
            mockCallbackUpdate();

            bot.consume(update);

            verify(callbackService).handle(update);
            verifyNoInteractions(navigator);
            verifyNoInteractions(sessionsService);
        }

        @Test
        @DisplayName("Should catch and log exceptions")
        void shouldCatchExceptions() {
            when(update.hasMessage()).thenThrow(new RuntimeException("Test exception"));

            bot.consume(update);

            verifyNoInteractions(navigator);
        }
    }

    @Nested
    @DisplayName("processUpdate")
    class ProcessUpdate {

        @Test
        @DisplayName("Should handle callback query first")
        void shouldHandleCallbackFirst() {
            mockCallbackUpdate();

            bot.consume(update);

            verify(callbackService).handle(update);
            verifyNoInteractions(navigator);
            verifyNoInteractions(sessionsService);
        }

        @Test
        @DisplayName("Should create new session for new user")
        void shouldCreateNewSessionForNewUser() {
            mockMessageUpdate();
            var session = mockNewSession();

            bot.consume(update);

            verify(sessionsService).getOrCreateSession(CHAT_ID);
            verify(sessionsService).save(session);
            verify(navigator).showInitMessage(any(MessageData.class));
            verifyNoInteractions(callbackService);
            verify(navigator, never()).navigate(any());
        }

        @Test
        @DisplayName("Should navigate for existing session")
        void shouldNavigateForExistingSession() {
            mockMessageUpdate();
            mockExistingSession();

            var session = new UserSessionContext();
            session.setNew(false);
            when(sessionsService.getOrCreateSession(CHAT_ID)).thenReturn(session);

            bot.consume(update);

            verify(sessionsService).getOrCreateSession(CHAT_ID);
            verify(navigator).navigate(update);
            verify(navigator, never()).showInitMessage(any());
            verify(sessionsService, never()).save(any());
        }

        @Test
        @DisplayName("Should ignore update without message and without callback")
        void shouldIgnoreUpdateWithoutMessage() {
            mockUpdateWithoutMessage();

            bot.consume(update);

            verifyNoInteractions(navigator);
            verifyNoInteractions(sessionsService);
            verifyNoInteractions(callbackService);
        }
    }

    @Nested
    @DisplayName("Session management")
    class SessionManagement {

        @Test
        @DisplayName("Should save session after setting new flag to false")
        void shouldSaveSessionAfterSettingNewFlag() {
            mockMessageUpdate();
            var session = mockNewSession();

            bot.consume(update);

            assertThat(session.isNew()).isFalse();
            verify(sessionsService).save(session);
        }

        @Test
        @DisplayName("Should not save session when not new")
        void shouldNotSaveExistingSession() {
            mockMessageUpdate();
            mockExistingSession();

            var session = new UserSessionContext();
            session.setNew(false);
            when(sessionsService.getOrCreateSession(CHAT_ID)).thenReturn(session);

            bot.consume(update);

            verify(sessionsService, never()).save(any());
        }
    }

    @Test
    @DisplayName("Should create MessageData from message")
    void shouldCreateMessageData() {
        var text = "/start";
        mockMessageUpdate();
        when(message.getText()).thenReturn(text);
        mockNewSession();

        var session = new UserSessionContext();
        session.setNew(true);
        when(sessionsService.getOrCreateSession(CHAT_ID)).thenReturn(session);

        bot.consume(update);

        verify(navigator).showInitMessage(argThat(messageData ->
                messageData.getChatId() == CHAT_ID &&
                        messageData.getMessageId().equals(message.getMessageId())
                        && messageData.getText().equals(text)
        ));
    }

    @Test
    @DisplayName("Should not throw when processing fails")
    void shouldNotThrowWhenFails() {
        when(update.hasMessage()).thenThrow(new RuntimeException("Test error"));

        bot.consume(update);

        verifyNoInteractions(navigator);
        verifyNoInteractions(sessionsService);
    }


    private void mockMessageUpdate() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(CHAT_ID);
    }

    private void mockUpdateWithoutMessage() {
        when(update.hasMessage()).thenReturn(false);
        when(update.getCallbackQuery()).thenReturn(null);
    }

    private UserSessionContext mockNewSession() {
        var session = new UserSessionContext();
        session.setNew(true);
        when(sessionsService.getOrCreateSession(CHAT_ID)).thenReturn(session);
        return session;
    }

    private UserSessionContext mockExistingSession() {
        var session = new UserSessionContext();
        session.setNew(false);
        when(sessionsService.getOrCreateSession(CHAT_ID)).thenReturn(session);
        return session;
    }

    private void mockCallbackUpdate() {
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
    }
}
