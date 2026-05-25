package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.IntervalType;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.CompletableMessageDataHandler;
import vacancy_tracker.services.telegram.command.settings.notification.SetDailyNotificationCommand;
import vacancy_tracker.services.telegram.command.settings.notification.SetHoursNotificationCommand;
import vacancy_tracker.services.telegram.command.settings.notification.SetWeeklyNotificationCommand;

import java.util.Optional;

import static vacancy_tracker.model.telegram.IntervalType.*;

@Component
public class NotificationIntervalCallbackHandler extends ParsingDataCallbackHandler<IntervalType> {

    public static final String KEY = NotificationSettingCallbackKeys.SET_INTERVAL.getKey();

    private final SetHoursNotificationCommand setHoursIntervalCommand;
    private final SetWeeklyNotificationCommand setWeeklyNotificationCommand;
    private final SetDailyNotificationCommand setDailyNotificationCommand;

    protected NotificationIntervalCallbackHandler(CompletableMessageDataHandler setNotificationIntervalCommand,
                                                  SetHoursNotificationCommand setHoursIntervalCommand,
                                                  SetWeeklyNotificationCommand setWeeklyNotificationCommand,
                                                  SetDailyNotificationCommand setDailyNotificationCommand) {
        super(KEY, setNotificationIntervalCommand);

        this.setHoursIntervalCommand = setHoursIntervalCommand;
        this.setWeeklyNotificationCommand = setWeeklyNotificationCommand;
        this.setDailyNotificationCommand = setDailyNotificationCommand;
    }

    @Override
    protected Optional<IntervalType> tryCastSelectedValue(String value) {
        if (value.equals(HOURS.getKey())) return Optional.of(HOURS);
        if (value.equals(DAILY.getKey())) return Optional.of(DAILY);
        if (value.equals(WEEKLY.getKey())) return Optional.of(WEEKLY);
        return Optional.empty();
    }

    @Override
    public void handleCastedData(IntervalType data, MessageData messageData) {
        switch (data) {
            case HOURS -> setHoursIntervalCommand.execute(messageData);
            case DAILY -> setDailyNotificationCommand.execute(messageData);
            case WEEKLY -> setWeeklyNotificationCommand.execute(messageData);
        }
    }
}