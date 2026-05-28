package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.filter.SetMinSalaryCommand;

import java.util.Optional;

@Component
@Slf4j
public class SetMinSalaryCallbackHandler extends ParsingDataCallbackHandler<Integer> {

    private static final String KEY = FilterSettingsCallbackKeys.SET_MIN_SALARY.getKey();

    public SetMinSalaryCallbackHandler(SetMinSalaryCommand setMinSalaryCommand) {
        super(KEY, setMinSalaryCommand);
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }
}
