package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.CommonCallbacks;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.filter.SetSearchingTextCommand;

import java.util.Optional;

@Component
public class SetSearchingTextCallbackHandler extends ParsingDataCallbackHandler<String> {

    private static final String KEY = FilterOptions.KEYWORDS.getCallback();

    public SetSearchingTextCallbackHandler(SetSearchingTextCommand command) {
        super(KEY, command);
    }

    @Override
    protected Optional<String> tryCastSelectedValue(String value) {
        if (value.equals(CommonCallbacks.NULL.getKey())) {
            return Optional.of("");
        }
        return Optional.of(value);
    }
}
