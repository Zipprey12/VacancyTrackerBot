package vacancy_tracker.services.telegram.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import vacancy_tracker.model.telegram.callback.CommonCallbacks;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.message.MessageSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CallbackServiceImpl implements CallbackService {

    private final Map<String, CallbackHandler> callbackHandlers = new HashMap<>();

    private final MessageSender sender;

    public CallbackServiceImpl(List<CallbackHandler> callbackHandlers, MessageSender sender) {
        callbackHandlers.forEach(handler -> {
            handler.setAnswerCallback(sender::answerCallback);
            this.callbackHandlers.put(handler.getKey(), handler);
        });
        this.sender = sender;
    }

    @Override
    public void handle(Update update) {
        var callback = update.getCallbackQuery();
        if (callback == null) {
            throw new IllegalArgumentException("Update не содержит Callback");
        }

        log.debug("Вызван callback: {}.", callback.getData());
        var key = getKey(callback);
        if (!key.equals(CommonCallbacks.IGNORE.getKey())) {
            callHandler(callback, key);
        } else {
            sender.answerCallback(callback.getId());
        }
    }

    private void callHandler(CallbackQuery callback, String key) {
        var handler = callbackHandlers.get(key);
        if (handler != null) {
            handler.execute(callback);
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
