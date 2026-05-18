package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationSettingCallbackKeys {

    ENABLED("enable_notification"),
    SET_INTERVAL("set_notification_interval"),
    SET_EMPTY_NOTIFY("set_empty_notify"),
    CANCEL_CHANGE("cancel_notification_change");

    private final String key;
}
