package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.model.telegram.notification.IntervalType;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.SetNotificationIntervalCommand;

import java.util.Optional;

import static vacancy_tracker.model.telegram.notification.IntervalType.*;

@Component
public class NotificationIntervalCallbackHandler extends ParsingDataCallbackHandler<IntervalType> {

    public static final String KEY = NotificationSettingCallbackKeys.SET_INTERVAL.getKey();

    protected NotificationIntervalCallbackHandler(SetNotificationIntervalCommand setNotificationIntervalCommand) {
        super(KEY, setNotificationIntervalCommand);
    }

    @Override
    protected Optional<IntervalType> tryCastSelectedValue(String value) {
        if (value.equals(HOURS.getKey())) return Optional.of(HOURS);
        if (value.equals(DAILY.getKey())) return Optional.of(DAILY);
        if (value.equals(WEEKLY.getKey())) return Optional.of(WEEKLY);
        return Optional.empty();
    }
}