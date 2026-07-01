package vacancy_tracker.services.telegram.navigation;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import vacancy_tracker.model.telegram.dto.MessageData;

@Component
public interface BotNavigator {

    void navigate(Update update);

    void showInitMessage(MessageData message);

    void showHelpMessage(MessageData message);
}
