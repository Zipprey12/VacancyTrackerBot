package vacancy_tracker.services.telegram.command.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.MessageCommand;
import vacancy_tracker.services.telegram.events.FilterSettingsEvent;

@Component
@RequiredArgsConstructor
public class FiltersInterceptingCompletionHandler implements CommandCompletionHandler {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onComplete(MessageCommand command, MessageData messageData) {
        if (command instanceof InputInterceptingCommand) {
            eventPublisher.publishEvent(new FilterSettingsEvent(command, messageData, false));
        } else {
            eventPublisher.publishEvent(new FilterSettingsEvent(null, messageData, false));
        }
    }
}
