package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.handlers.CommandCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;

@Slf4j
public abstract class InputInterceptingCommand extends MessageCommand {

    @Getter(AccessLevel.PROTECTED)
    private final SessionsService sessionsService;

    @Getter(AccessLevel.PROTECTED)
    private final InputInterceptor inputInterceptor;

    protected InputInterceptingCommand(String key,
                                       String description,
                                       MessagePublisher publisher,
                                       CommandCompletionHandler handler,
                                       InputInterceptor inputInterceptor,
                                       SessionsService sessionsService) {
        super(key, description, publisher, handler);

        inputInterceptor.setCommand(this);

        this.sessionsService = sessionsService;
        this.inputInterceptor = inputInterceptor;
    }

    @Override
    public final void execute(MessageData message) {
        var text = message.getText();
        if (text != null) {
            var parameters = getParameters(text);
            if (!parameters.isEmpty()) {
                handleWithParameters(message, parameters);
                return;
            }
        }

        enableInterceptor(message.getChatId());
        super.execute(message);
    }

    @Override
    public void endExecution(MessageData message) {
        super.endExecution(message);
        disableInterceptor(message.getChatId());
    }

    protected void handleWithParameters(MessageData message, String parameter) {
        var id = message.getChatId();
        inputInterceptor.tryHandleInput(parameter, id);
    }

    public void enableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.setInputInterceptor(inputInterceptor);
        sessionsService.save(session);
    }

    public void disableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.deleteInterceptor();
        sessionsService.save(session);
        log.debug("Отключен перехватчик ввода для {}", getKey());
    }

    protected String getParameters(String fullCommand) {
        return fullCommand.replaceFirst(getKey(), "").trim();
    }
}
