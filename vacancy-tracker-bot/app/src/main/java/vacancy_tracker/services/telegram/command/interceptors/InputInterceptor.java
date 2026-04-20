package vacancy_tracker.services.telegram.command.interceptors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.session.SessionsService;

@RequiredArgsConstructor
public abstract class InputInterceptor {

    private final SessionsService sessionsService;

    @Setter(AccessLevel.PROTECTED)
    private boolean unsubscribeAfterPerform = true;

    public abstract void perform(Message message);

    public void processMessage(Message message) {
        perform(message);

        if (unsubscribeAfterPerform) {
            var session = sessionsService.getSession(message.getChatId());
            session.deleteInterceptor();
        }
    }
}
