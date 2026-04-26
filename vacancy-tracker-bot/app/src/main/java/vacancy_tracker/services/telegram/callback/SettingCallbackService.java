package vacancy_tracker.services.telegram.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SettingCallbackService implements CallbackService {

    private final Map<String, CallbackHandler> callbackHandlers;

    public SettingCallbackService(@Qualifier("callbackHandlers") List<CallbackHandler> callbackHandlers) {
        this.callbackHandlers = callbackHandlers.stream()
                .collect(Collectors.toMap(
                        CallbackHandler::getCallbackKey,
                        handler -> handler
                ));
    }

    @Override
    public void handle(Update update) {
        var callback = update.getCallbackQuery();
        if (callback == null) {
            throw new IllegalArgumentException("Update не содержит Callback");
        }
        var key = callback.getData();
        var handler = callbackHandlers.get(key);
        if (handler != null) {
            handler.handle(callback);

        } else {
            log.warn("Был вызван Callback {}, для которого нет обработчика", key);
        }
    }
}
