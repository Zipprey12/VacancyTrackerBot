package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.search.SetMaxSalaryCommand;

import java.util.Optional;

@Component
public class SetMaxSalaryCallbackHandler extends ParsingDataCallbackHandler<Integer> {

    private static final String KEY = FilterSettingsCallbackKeys.SET_MAX_SALARY.getKey();
    private final SetMaxSalaryCommand setMaxSalaryCommand;

    public SetMaxSalaryCallbackHandler(SetMaxSalaryCommand setMaxSalaryCommand) {
        super(KEY, setMaxSalaryCommand);
        this.setMaxSalaryCommand = setMaxSalaryCommand;
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }

    @Override
    public void handleCastedData(Integer data, MessageData messageData) {
        setMaxSalaryCommand.handleWithParameter(messageData, data);
    }
}
