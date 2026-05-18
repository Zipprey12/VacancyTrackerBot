package vacancy_tracker.services.telegram.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vacancy_tracker.services.telegram.command.settings.notification.SetNotificationSettingsCommand;
import vacancy_tracker.services.telegram.command.settings.search.SetSearchSettingsCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettingsChangingEventListener {

    private final SetSearchSettingsCommand setSearchSettingsCommand;
    private final SetNotificationSettingsCommand setNotificationCommand;

    @Async
    @EventListener
    public void handleFiltersChangingEvent(FilterSettingsEvent filterSettingsEvent) {
        var data = filterSettingsEvent.getMessageData();
        setSearchSettingsCommand.execute(data);
    }

    @Async
    @EventListener
    public void handleNotificationSettingsChangingEvent(NotificationSettingEvent event) {
        var data = event.getMessageData();
        setNotificationCommand.execute(data);
    }
}