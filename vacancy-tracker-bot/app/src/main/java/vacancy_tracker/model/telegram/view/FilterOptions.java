package vacancy_tracker.model.telegram.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.*;

@Getter
@RequiredArgsConstructor
public enum FilterOptions implements CallBackDataProvider {
    KEYWORDS("Ключевые слова", SET_TEXT.getKey()),
    LOCATION("Местоположение", "change_location"),
    EXPERIENCE("Опыт", SET_EXPERIENCE.getKey()),
    MIN_SALARY("Зарплата от", SET_MIN_SALARY.getKey()),
    MAX_SALARY("Зарплата до", SET_MAX_SALARY.getKey()),
    RESET_FILTERS("Сбросить фильтр", "reset_filters");

    private final String text;
    private final String callback;
}