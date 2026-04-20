package vacancy_tracker.services.telegram.command.interceptors;

import lombok.AccessLevel;
import lombok.Getter;
import vacancy_tracker.services.telegram.command.SimpleMessageCommand;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;

public abstract class InputInterceptingCommand extends SimpleMessageCommand {

    @Getter(AccessLevel.PROTECTED)
    private final SessionsService sessionsService;

    @Getter(AccessLevel.PROTECTED)
    private final InputInterceptor inputInterceptor;

    public InputInterceptingCommand(MessageSender sender, SessionsService manager, InputInterceptor inputInterceptor) {
        super(sender);

        sessionsService = manager;
        this.inputInterceptor = inputInterceptor;
    }

    public void enableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.setInputInterceptor(inputInterceptor);
    }

    public void disableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.deleteInterceptor();
    }
}
