package vacancy_tracker.services.telegram.navigation;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public interface BotNavigator {

    void navigate(Update update);

    void showInitMessage(Message message);

    void showHelpMessage(Message message);
}
