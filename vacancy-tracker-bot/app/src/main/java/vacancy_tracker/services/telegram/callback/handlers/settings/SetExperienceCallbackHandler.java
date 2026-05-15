package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.command.settings.SetExperienceCommand;
import vacancy_tracker.services.telegram.settings.SettingsService;

import java.util.Optional;

@Component
@Slf4j
public class SetExperienceCallbackHandler extends FiltersParsingCallbackHandler<Float> {

    private static final String KEY = FilterSettingsCallbackKeys.SET_EXPERIENCE.getKey();

    public SetExperienceCallbackHandler(SetExperienceCommand setExperienceCommand,
                                        SettingsService settingsService) {
        super(KEY, settingsService, setExperienceCommand);
    }

    @Override
    protected Optional<Float> tryCastSelectedValue(String value) {
        return StringUtil.parseFloat(value);
    }

    @Override
    protected void changeSettings(Float value, VacancySearchFilter filter) {
        filter.setExperience(value);
    }
}