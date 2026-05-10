package vacancy_tracker.services.telegram.callback.handlers.settings;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.SimpleMessageCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;

@Component
public class SetMaxSalaryCallbackHandler extends SimpleMessageCallbackHandler {

    private static final String KEY = FilterOptions.MAX_SALARY.getCallback();

    public SetMaxSalaryCallbackHandler(@Qualifier("setMaxSalaryCommand") SearchFiltersCommand setMaxSalaryCommand) {
        super(KEY, setMaxSalaryCommand);
    }
}
