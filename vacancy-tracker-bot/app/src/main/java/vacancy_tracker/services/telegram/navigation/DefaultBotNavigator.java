package vacancy_tracker.services.telegram.navigation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.SimpleMessageCommand;
import vacancy_tracker.services.telegram.command.callers.MessageCommandCaller;
import vacancy_tracker.services.telegram.session.SessionsService;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class DefaultBotNavigator implements BotNavigator {

    private final SimpleMessageCommand initCommand;
    private final SimpleMessageCommand helpCommand;

    private final SessionsService sessionsService;
    private final MessageCommandCaller executor;

    @Override
    public void navigate(Update update) {
        var message = update.getMessage();
        if (message == null) {
            return;
        }

        var messageData = MessageData.create(message);
        var chatId = message.getChatId();
        var text = message.getText();

        if (text.startsWith("/")) {
            clearInterceptor(chatId);
            executeOrHelp(messageData);
            return;
        }

        var inputInterceptor = sessionsService.getSession(chatId).getInputInterceptor();
        if (inputInterceptor != null) {
            inputInterceptor.processInput(messageData);
            return;
        }

        executeOrHelp(messageData);
    }

    @Override
    public void showInitMessage(MessageData message) {
        CompletableFuture.runAsync(() -> {
            initCommand.execute(message);
            helpCommand.execute(message);
        });
    }

    @Override
    public void showHelpMessage(MessageData message) {
        CompletableFuture.runAsync(() -> helpCommand.execute(message));
    }

    private void executeOrHelp(MessageData messageData) {
        executor.execute(messageData)
                .thenAccept(executed -> {
                    if (Boolean.FALSE.equals(executed)) {
                        helpCommand.execute(messageData);
                    }
                });
    }

    private void clearInterceptor(long chatId) {
        var session = sessionsService.getSession(chatId);
        if (session.getInputInterceptor() != null) {
            session.setInputInterceptor(null);
            sessionsService.save(session);
        }
    }
}