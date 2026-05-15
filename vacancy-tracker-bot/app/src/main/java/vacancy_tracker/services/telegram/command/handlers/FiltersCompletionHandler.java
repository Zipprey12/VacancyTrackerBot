package vacancy_tracker.services.telegram.command.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.MessageCommand;
import vacancy_tracker.services.telegram.events.FilterSettingsEvent;

@Component
@RequiredArgsConstructor
public class FiltersCompletionHandler implements CommandCompletionHandler {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onComplete(MessageCommand command, MessageData messageData) {
        eventPublisher.publishEvent(new FilterSettingsEvent(command, messageData, false));
    }
}
