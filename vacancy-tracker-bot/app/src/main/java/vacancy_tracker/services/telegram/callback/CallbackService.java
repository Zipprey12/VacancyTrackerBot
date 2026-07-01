package vacancy_tracker.services.telegram.callback;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackService {
    void handle(Update update);
}
