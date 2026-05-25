package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vacancy_tracker.model.telegram.CallingSource;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.handlers.CommandCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;

import java.util.Objects;

@Slf4j
public abstract class InputInterceptingCommand<T> extends CompletableMessageCommand implements InputHandler<T> {

    @Getter(AccessLevel.PROTECTED)
    private final SessionsService sessionsService;

    @Getter(AccessLevel.PROTECTED)
    private final InputInterceptor<T> inputInterceptor;

    @Setter(AccessLevel.PROTECTED)
    private boolean triggerEvent = true;

    protected InputInterceptingCommand(String key,
                                       MessagePublisher publisher,
                                       CommandCompletionHandler handler,
                                       InputInterceptor<T> inputInterceptor,
                                       SessionsService sessionsService) {

        super(key, null, publisher, handler);
        inputInterceptor.setHandler(this);

        this.sessionsService = sessionsService;
        this.inputInterceptor = inputInterceptor;
    }

    protected InputInterceptingCommand(String key,
                                       String description,
                                       MessagePublisher publisher,
                                       CommandCompletionHandler handler,
                                       InputInterceptor<T> inputInterceptor,
                                       SessionsService sessionsService) {
        super(key, description, publisher, handler);

        inputInterceptor.setHandler(this);

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
    public void endExecution(MessageData message, boolean isSuccess) {
        super.endExecution(message, isSuccess);
        disableInterceptor(message.getChatId());
    }

    @Override
    public final void handleWithParameter(MessageData messageData, T parameters) {
        executeWithParameter(messageData, parameters);
        if (triggerEvent) {
            endExecution(messageData);
        }
    }

    @Override
    public void handleInvalidValue(MessageData messageData) {
        if (triggerEvent) {
            endExecution(messageData, false);
        }
        getPublisher().publish(createInvalidOutgoingMessage(messageData, null));
    }

    public void handleInvalidValue(MessageData messageData, String reason) {
        if (triggerEvent) {
            endExecution(messageData, false);
        }
        getPublisher().publish(createInvalidOutgoingMessage(messageData, reason));
    }

    protected abstract void executeWithParameter(MessageData messageData, T parameter);

    protected String getParameters(String fullCommand) {
        return fullCommand.replaceFirst(getKey(), "").trim();
    }

    protected void handleWithParameters(MessageData messageData, String parameter) {
        messageData.setText(parameter);
        inputInterceptor.processInput(messageData);
    }

    protected void enableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.setInputInterceptor(inputInterceptor);
        sessionsService.save(session);
    }

    protected void disableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.deleteInterceptor();
        sessionsService.save(session);
        log.debug("Отключен перехватчик ввода для {}", getKey());
    }

    private OutgoingMessage createInvalidOutgoingMessage(MessageData messageData, String text) {
        var message = new OutgoingMessage(messageData);
        message.setSource(CallingSource.CHAT);
        message.setText(Objects.requireNonNullElse(text, "Неверный формат данных"));
        return message;
    }
}