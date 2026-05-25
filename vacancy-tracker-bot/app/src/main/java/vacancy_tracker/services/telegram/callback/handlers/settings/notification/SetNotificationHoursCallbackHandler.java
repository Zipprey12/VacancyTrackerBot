package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.SetHoursNotificationCommand;

import java.time.Duration;
import java.util.Optional;

@Component
public class SetNotificationHoursCallbackHandler extends ParsingDataCallbackHandler<Integer> {

    public static final String KEY = NotificationSettingCallbackKeys.SET_HOURS.getKey();
    private final SetHoursNotificationCommand setHoursIntervalCommand;

    protected SetNotificationHoursCallbackHandler(SetHoursNotificationCommand setHoursIntervalCommand) {
        super(KEY, setHoursIntervalCommand);
        this.setHoursIntervalCommand = setHoursIntervalCommand;
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }

    @Override
    public void handleCastedData(Integer data, MessageData messageData) {
        var duration = Duration.ofHours(data.longValue());
        setHoursIntervalCommand.handleWithParameter(messageData, duration);
    }
}
