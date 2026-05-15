package vacancy_tracker.services.telegram.command.interceptors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.session.SessionsService;

@RequiredArgsConstructor
public abstract class InputInterceptor {

    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 100;

    private final SessionsService sessionsService;

    @Setter
    private InputInterceptingCommand command;

    @Setter(AccessLevel.PROTECTED)
    private boolean unsubscribeAfterPerform = true;

    @Setter(AccessLevel.PROTECTED)
    private boolean triggerEvent = true;

    protected abstract boolean tryHandlePreparedInput(String text, long chatId);

    protected abstract void perform(Message message);

    protected int getMinLength() {
        return MIN_LENGTH;
    }

    protected int getMaxLength() {
        return MAX_LENGTH;
    }

    public void processMessage(Message message) {
        perform(message);

        if (triggerEvent && command != null) {
            command.endExecution(MessageData.create(message));
        }

        if (unsubscribeAfterPerform) {
            var session = sessionsService.getSession(message.getChatId());
            session.deleteInterceptor();
        }
    }

    public boolean tryHandleInput(String text, long chatId) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String trimmed = text.trim();
        if (trimmed.length() < getMinLength() || trimmed.length() > getMaxLength()) {
            return false;
        }
        return tryHandlePreparedInput(trimmed, chatId);
    }
}
