package vacancy_tracker.services.telegram.command.settings;

import lombok.AccessLevel;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.SimpleMessageCommand;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;

public abstract class InputInterceptingCommand extends SimpleMessageCommand {

    @Getter(AccessLevel.PROTECTED)
    private final SessionsService sessionsService;

    @Getter(AccessLevel.PROTECTED)
    private final InputInterceptor inputInterceptor;
    
    protected InputInterceptingCommand(MessageSender sender, SessionsService sessionsService,
                                       InputInterceptor inputInterceptor) {
        super(sender);
        this.sessionsService = sessionsService;
        this.inputInterceptor = inputInterceptor;
    }

    protected abstract void handleOnlyCommandInput(Message message);

    @Override
    public void execute(Message message) {
        var text = message.getText();
        if (text == null) {
            return;
        }

        var parameters = getParameters(text);
        if (parameters == null) {
            handleOnlyCommandInput(message);
            enableInterceptor(message.getChatId());
        } else {
            handleParameters(message, parameters);
        }
    }

    protected void handleParameters(Message message, String parameter){
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
