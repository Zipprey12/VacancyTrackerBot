package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.callback.CommonCallbackKeys;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.command.settings.search.SetSearchingTextCommand;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;

import java.util.Optional;

@Component
public class SetSearchingTextCallbackHandler extends FiltersParsingCallbackHandler<String> {

    private static final String KEY = FilterOptions.KEYWORDS.getCallback();

    public SetSearchingTextCallbackHandler(SetSearchingTextCommand command,
                                           SearchFiltersService settingsService) {
        super(KEY, settingsService, command);
    }

    @Override
    protected void changeSettings(String value, VacancySearchFilter filter) {
        if (value.isEmpty()) {
            filter.setText(null);
        } else {
            filter.setText(value);
        }
    }

    @Override
    protected Optional<String> tryCastSelectedValue(String value) {
        if (value.equals(CommonCallbackKeys.NULL.getKey())) {
            return Optional.of("");
        }
        return Optional.of(value);
    }
}
