package vacancy_tracker.services.telegram.callback.handlers.settings;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;
import vacancy_tracker.services.telegram.settings.SettingsService;

import java.util.Optional;

@Component
public class SetMaxSalaryCallbackHandler extends FiltersParsingCallbackHandler<Integer> {

    private static final String KEY = FilterSettingsCallbackKeys.SET_MAX_SALARY.getKey();

    public SetMaxSalaryCallbackHandler(SearchFiltersCommand setMaxSalaryCommand,
                                       SettingsService settingsService) {
        super(KEY, settingsService, setMaxSalaryCommand);
    }

    @Override
    protected void changeSettings(Integer value, VacancySearchFilter filter) {
        filter.setMaxSalary(value <= 0 ? null : value);
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }
}
