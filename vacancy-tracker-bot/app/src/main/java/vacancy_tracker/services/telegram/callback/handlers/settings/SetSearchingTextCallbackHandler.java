package vacancy_tracker.services.telegram.callback.handlers.settings;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.SimpleMessageCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;

@Component
public class SetSearchingTextCallbackHandler extends SimpleMessageCallbackHandler {

    private static final String KEY = FilterOptions.KEYWORDS.getCallback();

    public SetSearchingTextCallbackHandler(@Qualifier("setSearchingTextCommand") SearchFiltersCommand setSearchingTextCommand) {
        super(KEY, setSearchingTextCommand);
    }
}
