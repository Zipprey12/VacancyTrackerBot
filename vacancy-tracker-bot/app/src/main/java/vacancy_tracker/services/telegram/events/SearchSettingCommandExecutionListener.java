package vacancy_tracker.services.telegram.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vacancy_tracker.services.telegram.command.settings.SetSearchSettingsCommand;
import vacancy_tracker.services.telegram.session.SessionsService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchSettingCommandExecutionListener {

    private final SetSearchSettingsCommand command;
    private final SessionsService sessionsService;

    @Async
    @EventListener
    public void handleSettingCommandExecutionEvent(FilterSettingsEvent filterSettingsEvent) {
        var data = filterSettingsEvent.getMessageData();
        command.processInput(data, !filterSettingsEvent.isInterceptorUsed());

        var session = sessionsService.getSession(data.getChatId());
        session.setLastSignificantMessage(data);

        sessionsService.save(session);
    }
}