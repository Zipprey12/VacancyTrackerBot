package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilterSettingsCallbackKeys {

    SET_REGION("select_region"),
    SET_TOWN("select_town"),
    CANCEL_CHANGE("cancel_filter_change"),
    RESET_TEXT("reset_searching_text"),
    SET_TEXT("select_searching_text"),
    SET_EXPERIENCE("select_experience"),
    SET_MIN_SALARY("select_min_salary"),
    SET_MAX_SALARY("select_max_salary"),
    RESET_ALL("reset_filters");

    private final String key;
}
