package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.command.settings.search.SetMinSalaryCommand;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;

import java.util.Optional;

@Component
@Slf4j
public class SetMinSalaryCallbackHandler extends FiltersParsingCallbackHandler<Integer> {

    private static final String KEY = FilterSettingsCallbackKeys.SET_MIN_SALARY.getKey();

    public SetMinSalaryCallbackHandler(SetMinSalaryCommand setMinSalaryCommand,
                                       SearchFiltersService settingsService) {
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
