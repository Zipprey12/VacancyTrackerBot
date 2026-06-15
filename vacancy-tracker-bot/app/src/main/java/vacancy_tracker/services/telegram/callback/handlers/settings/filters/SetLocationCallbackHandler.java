package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.settings.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.SimpleMessageCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.filter.SetRegionCommand;

@Slf4j
@Component
public class SetLocationCallbackHandler extends SimpleMessageCallbackHandler {

    public static final String KEY = FilterOptions.LOCATION.getCallback();

    public SetLocationCallbackHandler(SetRegionCommand setRegionCommand) {
        super(KEY, setRegionCommand);
    }
}