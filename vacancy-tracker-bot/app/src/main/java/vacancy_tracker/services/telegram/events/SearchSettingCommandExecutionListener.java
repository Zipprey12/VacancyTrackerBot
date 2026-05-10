package vacancy_tracker.services.telegram.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vacancy_tracker.services.telegram.command.settings.SetSearchSettingsCommand;
import vacancy_tracker.services.telegram.session.SessionsService;

@Component
@RequiredArgsConstructor
public class SearchSettingCommandExecutionListener {

    private final SetSearchSettingsCommand command;
    private final SessionsService sessionsService;

    @Async
    @EventListener
    public void handleSettingCommandExecutionEvent(SettingCommandExecutionEvent commandExecutionEvent) {
        var data = commandExecutionEvent.getMessageData();
        command.execute(data, !commandExecutionEvent.isInterceptorUsed());

        var session = sessionsService.getSession(data.getChatId());
        session.setLastSignificantMessage(data);
        sessionsService.save(session);
    }
}