package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.SetNotificationTimeCommand;

import java.time.LocalTime;
import java.util.Optional;

@Component
public class SetNotificationTimeCallbackHandler extends ParsingDataCallbackHandler<LocalTime> {

    private static final String KEY = NotificationSettingCallbackKeys.SET_TIME.getKey();

    protected SetNotificationTimeCallbackHandler(SetNotificationTimeCommand handler) {
        super(KEY, handler);
    }

    @Override
    protected Optional<LocalTime> tryCastSelectedValue(String value) {
        return StringUtil.parseTime(value);
    }
}
