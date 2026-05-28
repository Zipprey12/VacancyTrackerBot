package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VacanciesCallbackKeys {

    GET_VACANCIES("get_vacancies");

    private final String key;

}
