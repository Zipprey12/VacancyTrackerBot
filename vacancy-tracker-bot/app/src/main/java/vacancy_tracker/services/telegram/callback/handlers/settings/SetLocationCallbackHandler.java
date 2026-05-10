package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.SimpleMessageCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;

@Slf4j
@Component
public class SetLocationCallbackHandler extends SimpleMessageCallbackHandler {

    public static final String KEY = FilterOptions.LOCATION.getCallback();

    public SetLocationCallbackHandler(@Qualifier("setLocationCommand") SearchFiltersCommand setLocationCommand) {
        super(KEY, setLocationCommand);
    }
}
