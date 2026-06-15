package vacancy_tracker.services.telegram.command.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;
import vacancy_tracker.services.telegram.events.FilterSettingsEvent;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class FiltersChangingCompletionHandler implements CommandCompletionHandler {

    private final SearchFiltersService service;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    @Override
    public void onComplete(Identifiable command, MessageData messageData) {
        var chatId = messageData.getChatId();
        var filters = service.get(chatId);
        eventPublisher.publishEvent(new FilterSettingsEvent(command, messageData, false, filters));

        CompletableFuture.runAsync(() -> updateNotificationSettings(chatId));
    }

    public void updateNotificationSettings(long chatId) {
        var settings = notificationService.get(chatId);
        settings.setLastNotificationAt(null);
        notificationService.save(chatId, settings);
    }
}