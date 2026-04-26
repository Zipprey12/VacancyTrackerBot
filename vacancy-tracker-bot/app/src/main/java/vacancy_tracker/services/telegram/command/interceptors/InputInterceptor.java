package vacancy_tracker.services.telegram.command.interceptors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.session.SessionsService;

@RequiredArgsConstructor
public abstract class InputInterceptor {

    private final SessionsService sessionsService;

    @Setter
    private InputInterceptingCommand command;

    @Setter(AccessLevel.PROTECTED)
    private boolean unsubscribeAfterPerform = true;

    @Setter(AccessLevel.PROTECTED)
    private boolean triggerEvent = true;

    public abstract boolean tryHandleInput(String text, long chatId);

    protected abstract void perform(Message message);

    public void processMessage(Message message) {
        perform(message);

        if (triggerEvent && command != null) {
            var session = sessionsService.getSession(message.getChatId());
            var lastMessage = session.getLastSignificantMessage();
            if (lastMessage != null) {
                command.handleInputEnd(lastMessage);
            }
        }

        if (unsubscribeAfterPerform) {
            var session = sessionsService.getSession(message.getChatId());
            session.deleteInterceptor();
        }
    }
}
