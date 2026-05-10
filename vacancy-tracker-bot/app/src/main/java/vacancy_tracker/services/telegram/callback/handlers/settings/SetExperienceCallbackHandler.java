package vacancy_tracker.services.telegram.callback.handlers.settings;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.callback.handlers.SimpleMessageCallbackHandler;
import vacancy_tracker.services.telegram.command.MessageDataHandlerCommand;

@Component
public class SetExperienceCallbackHandler extends SimpleMessageCallbackHandler {

    private static final String KEY = FilterOptions.EXPERIENCE.getCallback();

    public SetExperienceCallbackHandler(@Qualifier("setExperienceCommand") MessageDataHandlerCommand handler) {
        super(KEY, handler);
    }
}
