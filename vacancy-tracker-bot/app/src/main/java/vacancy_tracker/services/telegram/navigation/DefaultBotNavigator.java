package vacancy_tracker.services.telegram.navigation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import vacancy_tracker.model.telegram.dto.MessageData;
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

        var messageData = MessageData.create(message);
        if (!executor.execute(messageData)) {
            helpCommand.processInput(messageData, false);
        }
    }

    @Override
    public void showInitMessage(MessageData message) {
        initCommand.processInput(message, false);
        helpCommand.processInput(message, false);
    }

    @Override
    public void showHelpMessage(MessageData message) {
        helpCommand.processInput(message, false);
    }
}
