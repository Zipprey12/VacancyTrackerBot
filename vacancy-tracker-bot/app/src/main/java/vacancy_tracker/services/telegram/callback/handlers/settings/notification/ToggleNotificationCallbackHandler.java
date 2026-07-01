package vacancy_tracker.services.telegram.callback.handlers.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.notification.ToggleNotificationCommand;
import vacancy_tracker.services.util.StringUtil;

import java.util.Optional;

@Component
public class ToggleNotificationCallbackHandler extends ParsingDataCallbackHandler<Boolean> {

    protected ToggleNotificationCallbackHandler(ToggleNotificationCommand command) {
        super(NotificationSettingCallbackKeys.ENABLED.getKey(), command);
    }

    @Override
    protected Optional<Boolean> tryCastSelectedValue(String value) {
        return StringUtil.parseBoolean(value);
    }
}
