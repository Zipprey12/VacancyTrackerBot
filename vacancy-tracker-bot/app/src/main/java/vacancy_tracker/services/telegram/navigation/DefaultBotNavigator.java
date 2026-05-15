package vacancy_tracker.services.telegram.navigation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.MessageCommand;
import vacancy_tracker.services.telegram.command.executors.MessageCommandExecutor;
import vacancy_tracker.services.telegram.session.SessionsService;

@Component
@RequiredArgsConstructor
public class DefaultBotNavigator implements BotNavigator {

    private final MessageCommand initCommand;
    private final MessageCommand helpCommand;

    private final SessionsService sessionsService;
    private final MessageCommandExecutor executor;

    @Override
    public void navigate(Update update) {
        var message = update.getMessage();
        if (message == null) {
            return;
        }

        var session = sessionsService.getSession(message.getChatId());
        var inputInterceptor = session.getInputInterceptor();
        var messageData = MessageData.create(message);

        if (message.getText().startsWith("/")) {
            executeOrHelp(messageData);

            if (inputInterceptor != null) {
                session.setInputInterceptor(null);
                sessionsService.save(session);
            }
            return;
        }

        if (inputInterceptor != null) {
            inputInterceptor.processMessage(message);
            return;
        }
        executeOrHelp(messageData);
    }

    @Override
    public void showInitMessage(MessageData message) {
        initCommand.execute(message);
        helpCommand.execute(message);
    }

    @Override
    public void showHelpMessage(MessageData message) {
        helpCommand.execute(message);
    }

    private void executeOrHelp(MessageData messageData) {
        if (!executor.execute(messageData)) {
            helpCommand.execute(messageData);
        }
    }
}