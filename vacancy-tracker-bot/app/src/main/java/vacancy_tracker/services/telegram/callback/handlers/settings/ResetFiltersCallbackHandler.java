package vacancy_tracker.services.telegram.callback.handlers.settings;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.ResetFiltersCommand;
import vacancy_tracker.services.telegram.settings.SettingsService;

import java.util.Optional;

@Component
public class ResetFiltersCallbackHandler extends ParsingDataCallbackHandler<Boolean> {

    public static final String KEY = FilterOptions.RESET_FILTERS.getCallback();

    private final SettingsService settingsService;

    public ResetFiltersCallbackHandler(ResetFiltersCommand resetSettingsCommand,
                                       SettingsService settingsService) {
        super(KEY, resetSettingsCommand);

        this.settingsService = settingsService;
    }

    @Override
    protected Optional<Boolean> tryCastSelectedValue(String value) {
        return Optional.of(true);
    }

    @Override
    public void handleCastedData(Boolean data, MessageData messageData) {
        settingsService.deleteFilters(messageData.getChatId());
        getHandler().endExecution(messageData);
    }
}
