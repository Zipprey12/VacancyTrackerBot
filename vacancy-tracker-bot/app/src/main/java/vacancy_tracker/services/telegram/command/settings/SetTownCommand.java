package vacancy_tracker.services.telegram.command.settings;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersInterceptingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.SetTownInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.formatters.TownsSelectionMessageFormatter;

@Component
public class SetTownCommand extends InputInterceptingCommand {

    public static final String KEY = "/set_town";
    public static final String DESCRIPTION = "Установка города поиска";

    private final SettingsService settingsService;
    private final TownsSelectionMessageFormatter formatter;

    public SetTownCommand(SendingAndUpdatingMessagePublisher publisher,
                          FiltersInterceptingCompletionHandler handler,
                          SessionsService sessionsService,
                          SettingsService settingsService,
                          TownsSelectionMessageFormatter formatter) {
        super(KEY,
                DESCRIPTION,
                publisher,
                handler,
                new SetTownInterceptor(publisher.getSender(), sessionsService, settingsService, formatter),
                sessionsService);

        this.settingsService = settingsService;
        this.formatter = formatter;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = settingsService.getFilters(messageData.getChatId());
        var location = settings.getLocation();
        formatter.fillMessage(messageData, location);
    }
}
