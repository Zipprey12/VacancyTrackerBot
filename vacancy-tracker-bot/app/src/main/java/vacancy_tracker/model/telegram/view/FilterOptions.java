package vacancy_tracker.model.telegram.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilterOptions implements CallBackDataProvider{
    KEYWORDS("Ключевые слова", "change_search_text"),
    LOCATION("Местоположение", "change_location"),
    EXPERIENCE("Опыт", "change_min_experience"),
    MIN_SALARY("Зарплата от", "change_min_salary"),
    MAX_SALARY("Зарплата до", "change_max_salary"),
    RESET_FILTERS("Сбросить фильтр", "reset_filters");

    private final String text;
    private final String callback;
}