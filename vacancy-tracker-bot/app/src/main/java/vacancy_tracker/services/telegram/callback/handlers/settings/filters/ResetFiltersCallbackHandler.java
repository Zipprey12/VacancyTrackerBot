package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.settings.FilterOptions;
import vacancy_tracker.model.telegram.settings.ResetFilterFieldType;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.filter.ResetFiltersCommand;

import java.util.Optional;

@Component
public class ResetFiltersCallbackHandler extends ParsingDataCallbackHandler<ResetFilterFieldType> {

    public static final String KEY = FilterOptions.RESET_FILTERS.getCallback();

    public ResetFiltersCallbackHandler(ResetFiltersCommand resetSettingsCommand) {
        super(KEY, resetSettingsCommand);
    }

    @Override
    protected Optional<ResetFilterFieldType> tryCastSelectedValue(String value) {
        try {
            return Optional.of(ResetFilterFieldType.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
