package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.SetHoursNotificationCommand;
import vacancy_tracker.services.util.StringUtil;

import java.time.Duration;
import java.util.Optional;

@Component
public class SetNotificationHoursCallbackHandler extends ParsingDataCallbackHandler<Duration> {

    public static final String KEY = NotificationSettingCallbackKeys.SET_HOURS.getKey();

    protected SetNotificationHoursCallbackHandler(SetHoursNotificationCommand setHoursIntervalCommand) {
        super(KEY, setHoursIntervalCommand);
    }

    @Override
    protected Optional<Duration> tryCastSelectedValue(String value) {
        return StringUtil.parseDuration(value);
    }
}
