package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;

public abstract class InputInterceptingCommand extends SendingAndUpdatingMessageCommand implements MessageDataHandlerCommand {

    @Getter(AccessLevel.PROTECTED)
    private final ApplicationEventPublisher eventPublisher;

    @Getter(AccessLevel.PROTECTED)
    private final SessionsService sessionsService;

    @Getter(AccessLevel.PROTECTED)
    private final InputInterceptor inputInterceptor;

    @Setter(AccessLevel.PROTECTED)
    private boolean markSignificantAfterExecution = false;

    protected InputInterceptingCommand(String key,
                                       String description,
                                       MessageSender sender,
                                       MessageEditor editor,
                                       ApplicationEventPublisher eventPublisher,
                                       SessionsService sessionsService,
                                       InputInterceptor inputInterceptor) {
        super(key, description, sender, editor);
        this.eventPublisher = eventPublisher;
        this.sessionsService = sessionsService;
        this.inputInterceptor = inputInterceptor;
        inputInterceptor.setCommand(this);
    }

    @Override
    public void execute(MessageData message, boolean shouldOverwrite) {
        var text = message.getText();
        if (text == null) {
            processInput(message, shouldOverwrite);
            return;
        }

        var parameters = getParameters(text);
        if (parameters.isEmpty()) {
            processInput(message, shouldOverwrite);
        } else {
            handleWithParameters(message, parameters);
        }
    }

    @Override
    public final void processInput(MessageData message, boolean shouldOverwrite) {
        var text = message.getText();
        if (text != null) {
            var parameters = getParameters(text);
            if (!parameters.isEmpty()) {
                handleWithParameters(message, parameters);
                return;
            }
        }

        super.processInput(message, shouldOverwrite);
        enableInterceptor(message.getChatId());

        if (markSignificantAfterExecution) {
            var session = sessionsService
                    .getSession(message.getChatId());
            session.setLastSignificantMessage(message);
        }
    }

    protected void handleWithParameters(MessageData message, String parameter) {
        var id = message.getChatId();
        inputInterceptor.tryHandleInput(parameter, id);
    }

    public void enableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.setInputInterceptor(inputInterceptor);
    }

    public void disableInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        session.deleteInterceptor();
    }

    protected String getParameters(String fullCommand) {
        return fullCommand.replaceFirst(getKey(), "").trim();
    }
}
