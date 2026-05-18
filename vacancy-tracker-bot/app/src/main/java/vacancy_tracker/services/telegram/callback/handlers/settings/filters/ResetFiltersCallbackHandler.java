package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.search.ResetFiltersCommand;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;

import java.util.Optional;

@Component
public class ResetFiltersCallbackHandler extends ParsingDataCallbackHandler<Boolean> {

    public static final String KEY = FilterOptions.RESET_FILTERS.getCallback();

    private final SearchFiltersService settingsService;

    public ResetFiltersCallbackHandler(ResetFiltersCommand resetSettingsCommand,
                                       SearchFiltersService settingsService) {
        super(KEY, resetSettingsCommand);

        this.settingsService = settingsService;
    }

    @Override
    protected Optional<Boolean> tryCastSelectedValue(String value) {
        return Optional.of(true);
    }

    @Override
    public void handleCastedData(Boolean data, MessageData messageData) {
        settingsService.delete(messageData.getChatId());
        getHandler().endExecution(messageData);
    }
}
