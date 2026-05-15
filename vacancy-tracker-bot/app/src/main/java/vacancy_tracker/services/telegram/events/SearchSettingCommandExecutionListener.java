package vacancy_tracker.services.telegram.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vacancy_tracker.services.telegram.command.settings.SetSearchSettingsCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchSettingCommandExecutionListener {

    private final SetSearchSettingsCommand command;

    @Async
    @EventListener
    public void handleSettingCommandExecutionEvent(FilterSettingsEvent filterSettingsEvent) {
        var data = filterSettingsEvent.getMessageData();
        command.execute(data);
    }
}