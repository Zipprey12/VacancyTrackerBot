package vacancy_tracker.model.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VacanciesSource {

    SUPER_JOB("Super Job"),
    TRUD_VSEM("Работа России (trudvsem.ru)");

    private final String name;
}