package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.model.telegram.MessageData;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;

public abstract class InputInterceptingCommand extends SendingMessageCommand implements MessageDataHandler {

    @Getter(AccessLevel.PROTECTED)
    private final ApplicationEventPublisher eventPublisher;

    @Getter(AccessLevel.PROTECTED)
    private final SessionsService sessionsService;

    @Getter(AccessLevel.PROTECTED)
    private final InputInterceptor inputInterceptor;

    protected InputInterceptingCommand(String key,
                                       String description,
                                       MessageSender sender,
                                       ApplicationEventPublisher eventPublisher,
                                       SessionsService sessionsService,
                                       InputInterceptor inputInterceptor) {
        super(key, description, sender);
        this.eventPublisher = eventPublisher;
        this.sessionsService = sessionsService;
        this.inputInterceptor = inputInterceptor;
        inputInterceptor.setCommand(this);
    }

    //todo переименовать
    public abstract void handleInputEnd(MessageData messageData);

    protected abstract void handle(MessageData messageData);

    @Override
    public void execute(Message message) {
        var text = message.getText();
        if (text == null) {
            return;
        }

        var parameters = getParameters(text);
        if (parameters == null) {
            var messageData = MessageData.create(message);
            execute(messageData);
        } else {
            handleWithParameters(message, parameters);
        }
    }

    @Override
    public void execute(MessageData messageData) {
        var chatId = messageData.getChatId();
        var session = sessionsService.getSession(chatId);
        session.setLastSignificantMessage(messageData);

        handle(messageData);
        enableInterceptor(chatId);
    }

    protected void handleWithParameters(Message message, String parameter) {
        var id = message.getChatId();
        inputInterceptor.tryHandleInput(parameter, id);
    }

    protected void enableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.setInputInterceptor(inputInterceptor);
    }

    protected void disableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.deleteInterceptor();
    }

    protected String getParameters(String fullCommand) {
        return fullCommand.replaceFirst(getKey(), "").trim();
    }
}
