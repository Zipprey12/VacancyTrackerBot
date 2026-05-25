package vacancy_tracker.services.telegram.command.settings.search;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.CallingSource;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.TextInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.TownsSelectionMessageFormatter;

@Component
public class SetTownCommand extends InputInterceptingCommand<String> {

    public static final String KEY = "/set_town";
    public static final String DESCRIPTION = "Установка города поиска";

    private final SearchFiltersService settingsService;
    private final TownsSelectionMessageFormatter formatter;

    public SetTownCommand(SendingAndUpdatingMessagePublisher publisher,
                          FiltersChangingCompletionHandler handler,
                          SessionsService sessionsService,
                          SearchFiltersService settingsService,
                          TownsSelectionMessageFormatter formatter) {
        super(KEY,
                DESCRIPTION,
                publisher,
                handler,
                new TextInterceptor(),
                sessionsService);

        this.settingsService = settingsService;
        this.formatter = formatter;
        setTriggerEvent(false);
    }

    @Override
    protected void executeWithParameter(MessageData messageData, String parameter) {
        var chatId = messageData.getChatId();
        var filters = settingsService.get(chatId);
        var location = filters.getLocation();
        var outgoingMessage = new OutgoingMessage(MessageData.builder()
                .source(CallingSource.CHAT)
                .chatId(chatId)
                .build());

        formatter.fillMessage(outgoingMessage, location, 0, parameter);
        getPublisher().publish(outgoingMessage);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = settingsService.get(messageData.getChatId());
        var location = settings.getLocation();
        formatter.fillMessage(messageData, location);
    }
}