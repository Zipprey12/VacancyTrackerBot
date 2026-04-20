package vacancy_tracker.services.telegram.navigation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.MessageBotCommand;
import vacancy_tracker.services.telegram.command.executors.MessageCommandExecutor;
import vacancy_tracker.services.telegram.session.SessionsService;

@Component
@RequiredArgsConstructor
public class DefaultBotNavigator implements BotNavigator {

    private final MessageBotCommand initCommand;
    private final MessageBotCommand helpCommand;

    private final SessionsService sessionsManager;
    private final MessageCommandExecutor executor;

    @Override
    public void navigate(Update update) {
        var message = update.getMessage();
        if (message == null) {
            return;
        }

        var id = message.getChatId();
        var session = sessionsManager.getSession(id);
        var inputInterceptor = session.getInputInterceptor();

        if (inputInterceptor != null) {
            inputInterceptor.processMessage(message);
            return;
        }

        if (!executor.execute(message)) {
            helpCommand.execute(message);
        }
    }

    @Override
    public void showInitMessage(Message message) {
        initCommand.execute(message);
        helpCommand.execute(message);
    }

    @Override
    public void showHelpMessage(Message message) {
        helpCommand.execute(message);
    }
}
