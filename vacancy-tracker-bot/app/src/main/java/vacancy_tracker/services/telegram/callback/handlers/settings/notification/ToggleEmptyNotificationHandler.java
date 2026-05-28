package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.ToggleEmptyNotifyCommand;

import java.util.Optional;

@Component
public class ToggleEmptyNotificationHandler extends ParsingDataCallbackHandler<Boolean> {

    protected ToggleEmptyNotificationHandler(ToggleEmptyNotifyCommand handler) {

        super(NotificationSettingCallbackKeys.SET_EMPTY_NOTIFY.getKey(), handler);
    }

    @Override
    protected Optional<Boolean> tryCastSelectedValue(String value) {
        return StringUtil.parseBoolean(value);
    }
}
