package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilterSettingsCallbackKeys {

    SELECT_REGION("select_region"),
    LOCATION_SELECTED("location_selected"),
    SELECT_TOWN("select_town");

    private final String key;
}
