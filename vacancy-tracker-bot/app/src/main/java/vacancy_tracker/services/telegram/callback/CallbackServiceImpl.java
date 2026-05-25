package vacancy_tracker.services.telegram.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import vacancy_tracker.model.telegram.callback.CommonCallbackKeys;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.message.MessageSender;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CallbackServiceImpl implements CallbackService {

    private final Map<String, CallbackHandler> callbackHandlers;
    private final MessageSender sender;

    public CallbackServiceImpl(List<CallbackHandler> callbackHandlers,
                               MessageSender sender) {
        this.callbackHandlers = callbackHandlers.stream()
                .collect(Collectors.toMap(
                        CallbackHandler::getKey,
                        handler -> handler
                ));
        this.sender = sender;
    }

    @Override
    public void handle(Update update) {
        var callback = update.getCallbackQuery();
        if (callback == null) {
            throw new IllegalArgumentException("Update не содержит Callback");
        }

        var key = getKey(callback);
        if (!key.equals(CommonCallbackKeys.IGNORE.getKey())) {
            callHandler(callback, key);
        }
        sender.answerCallback(callback.getId());
    }

    private void callHandler(CallbackQuery callback, String key) {
        var handler = callbackHandlers.get(key);
        if (handler != null) {
            handler.handle(callback);
        } else {
            log.warn("Был вызван Callback {}, для которого нет обработчика", callback.getData());
        }
    }

    private String getKey(CallbackQuery query) {
        var data = query.getData();
        var indexOfSeparator = data.indexOf(' ');
        if (indexOfSeparator == -1) {
            return data;
        }
        return data.substring(0, indexOfSeparator);
    }
}
