package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.SetWeeklyNotificationCommand;
import vacancy_tracker.services.util.StringUtil;

import java.util.Optional;

@Component
public class WeeklyNotificationCallbackHandler extends ParsingDataCallbackHandler<Integer> {

    private static final String KEY = NotificationSettingCallbackKeys.SET_WEEKLY.getKey();

    protected WeeklyNotificationCallbackHandler(SetWeeklyNotificationCommand handler) {
        super(KEY, handler);
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }
}
