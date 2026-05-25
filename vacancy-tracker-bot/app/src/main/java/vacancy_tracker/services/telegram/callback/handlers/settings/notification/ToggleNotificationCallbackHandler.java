package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.ToggleNotificationCommand;
import vacancy_tracker.services.telegram.notification.NotificationQueueService;
import vacancy_tracker.services.telegram.settings.NotificationService;

import java.util.Optional;

@Component
public class ToggleNotificationCallbackHandler extends ParsingDataCallbackHandler<Boolean> {

    private final NotificationService service;
    private final NotificationQueueService queueService;

    protected ToggleNotificationCallbackHandler(NotificationService service,
                                                ToggleNotificationCommand command,
                                                NotificationQueueService queueService) {
        super(NotificationSettingCallbackKeys.ENABLED.getKey(), command);
        this.service = service;
        this.queueService = queueService;
    }

    @Override
    protected Optional<Boolean> tryCastSelectedValue(String value) {
        return StringUtil.parseBoolean(value);
    }

    @SuppressWarnings("java:S5411")
    @Override
    public void handleCastedData(Boolean data, MessageData messageData) {
        var chatId = messageData.getChatId();
        var settings = service.get(chatId);
        settings.setEnabled(data);

        if (data) {
            queueService.schedule(chatId, settings);
        } else {
            queueService.cancel(chatId);
        }
        service.save(chatId, settings);
        getHandler().endExecution(messageData);
    }
}
