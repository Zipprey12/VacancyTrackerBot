package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;
import vacancy_tracker.services.telegram.settings.SettingsService;

import java.util.Optional;

@Component
@Slf4j
public class SetMinSalaryCallbackHandler extends FiltersParsingCallbackHandler<Integer> {

    private static final String KEY = FilterSettingsCallbackKeys.SET_MIN_SALARY.getKey();

    public SetMinSalaryCallbackHandler(SearchFiltersCommand setMinSalaryCommand,
                                       SettingsService settingsService) {
        super(KEY, settingsService, setMinSalaryCommand);
    }

    @Override
    protected void changeSettings(Integer value, VacancySearchFilter filter) {
        filter.setMinSalary(value <= 0 ? null : value);
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }
}
