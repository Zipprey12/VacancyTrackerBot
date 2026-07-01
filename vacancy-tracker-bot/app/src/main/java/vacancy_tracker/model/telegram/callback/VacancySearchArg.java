package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VacancySearchArg {

    SOURCE("src"),
    HAS_ANOTHER("more"),
    FROM("min");

    private final String key;

}
