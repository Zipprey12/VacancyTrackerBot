package vacancy_tracker.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import vacancy_tracker.services.telegram.navigation.BotNavigator;
import vacancy_tracker.services.telegram.session.SessionsService;

@Component
public class VacancyTrackerBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotNavigator navigator;
    private final SessionsService sessionsService;

    @Getter
    private final String botToken;

    private final TelegramClient client;

    public VacancyTrackerBot(@Value("${bot.data.token}") String botToken,
                             BotNavigator navigator,
                             SessionsService sessionsService) {
        this.botToken = botToken;
        this.client = new OkHttpTelegramClient(botToken);
        this.navigator = navigator;
        this.sessionsService = sessionsService;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if(!sessionsService.hasSession(message.getChatId())){
                sessionsService.addSession(message.getChatId());
                navigator.showInitMessage(message);
                return;
            }
            navigator.navigate(update);
        }
    }
}
