package vacancy_tracker.services.telegram.command.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;
import vacancy_tracker.services.telegram.events.NotificationSettingEvent;

@Component
@RequiredArgsConstructor
public class NotificationChangingCompletionHandler implements CommandCompletionHandler {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onComplete(Identifiable command, MessageData messageData) {
        eventPublisher.publishEvent(new NotificationSettingEvent(command, messageData));
    }
}
