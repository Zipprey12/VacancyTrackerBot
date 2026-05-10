package vacancy_tracker.services.telegram.callback.handlers.settings;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.SimpleMessageCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;

@Component
public class SetMinSalaryCallbackHandler extends SimpleMessageCallbackHandler {

    private static final String KEY = FilterOptions.MIN_SALARY.getCallback();

    public SetMinSalaryCallbackHandler(SearchFiltersCommand setMinSalaryCommand) {
        super(KEY, setMinSalaryCommand);
    }
}
