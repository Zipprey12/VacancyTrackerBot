package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.SetWeeklyNotificationCommand;

import java.util.Optional;

@Component
public class WeeklyNotificationCallbackHandler extends ParsingDataCallbackHandler<Integer> {

    private static final String KEY = NotificationSettingCallbackKeys.SET_WEEKLY.getKey();

    private final SetWeeklyNotificationCommand command;

    protected WeeklyNotificationCallbackHandler(SetWeeklyNotificationCommand handler) {
        super(KEY, handler);
        command = handler;
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }

    @Override
    public void handleCastedData(Integer data, MessageData messageData) {
        command.handleWithParameter(messageData, data);
    }
}
