package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.filter.SetExperienceCommand;

import java.util.Optional;

@Component
@Slf4j
public class SetExperienceCallbackHandler extends ParsingDataCallbackHandler<Float> {

    private static final String KEY = FilterSettingsCallbackKeys.SET_EXPERIENCE.getKey();

    public SetExperienceCallbackHandler(SetExperienceCommand setExperienceCommand) {
        super(KEY, setExperienceCommand);
    }

    @Override
    protected Optional<Float> tryCastSelectedValue(String value) {
        return StringUtil.parseFloat(value);
    }
}