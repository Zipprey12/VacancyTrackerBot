package vacancy_tracker.services.telegram.command.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;
import vacancy_tracker.services.telegram.events.FilterSettingsEvent;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;

@Component
@RequiredArgsConstructor
public class FiltersChangingCompletionHandler implements CommandCompletionHandler {

    private final SearchFiltersService service;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onComplete(Identifiable command, MessageData messageData) {
        var filters = service.get(messageData.getChatId());
        eventPublisher.publishEvent(new FilterSettingsEvent(command, messageData, false, filters));
    }
}