package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonCallbacks {

    VACANCIES("vacancies"),
    IGNORE("ignore"),
    NULL("null");

    private final String key;
}
