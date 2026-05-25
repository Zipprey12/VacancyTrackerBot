package vacancy_tracker.services.telegram.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettingsChangingEventListener {

    private final CompletableMessageCommand setSearchSettingsCommand;
    private final CompletableMessageCommand setNotificationSettingsCommand;

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
        setNotificationSettingsCommand.execute(data);
    }
}