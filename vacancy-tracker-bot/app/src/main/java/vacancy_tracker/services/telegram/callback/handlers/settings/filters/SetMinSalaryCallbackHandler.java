package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.search.SetMinSalaryCommand;

import java.util.Optional;

@Component
@Slf4j
public class SetMinSalaryCallbackHandler extends ParsingDataCallbackHandler<Integer> {

    private static final String KEY = FilterSettingsCallbackKeys.SET_MIN_SALARY.getKey();
    private final SetMinSalaryCommand setMinSalaryCommand;

    public SetMinSalaryCallbackHandler(SetMinSalaryCommand setMinSalaryCommand) {
        super(KEY, setMinSalaryCommand);
        this.setMinSalaryCommand = setMinSalaryCommand;
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }

    @Override
    public void handleCastedData(Integer data, MessageData messageData) {
        setMinSalaryCommand.handleWithParameter(messageData, data);
    }
}
