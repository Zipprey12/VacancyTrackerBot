package vacancy_tracker.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.CallbackService;
import vacancy_tracker.services.telegram.navigation.BotNavigator;
import vacancy_tracker.services.telegram.session.SessionsService;

@Component
public class VacancyTrackerBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotNavigator navigator;
    private final SessionsService sessionsService;
    private final CallbackService callbackService;

    @Getter
    private final String botToken;

    public VacancyTrackerBot(@Value("${bot.token}") String botToken,
                             BotNavigator navigator,
                             SessionsService sessionsService,
                             CallbackService callbackService) {
        this.botToken = botToken;
        this.navigator = navigator;
        this.sessionsService = sessionsService;
        this.callbackService = callbackService;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        var callback = update.getCallbackQuery();
        if (callback != null) {
            callbackService.handle(update);
            return;
        }

        if (update.hasMessage()) {
            var message = update.getMessage();
            var messageData = MessageData.create(message);

            if (!sessionsService.hasSession(message.getChatId())) {
                sessionsService.addSession(message.getChatId());
                navigator.showInitMessage(messageData);
                return;
            }
            navigator.navigate(update);
        }
    }
}
