package vacancy_tracker.model.api.dto;


import lombok.Data;
import vacancy_tracker.model.api.RequestType;
import vacancy_tracker.model.api.VacanciesSource;
import vacancy_tracker.model.api.Vacancy;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VacanciesResponse {

    private VacanciesSource source;
    private List<Vacancy> vacancies;

    private long offset;
    private boolean more;
    private long total;
    private boolean isSuccess;

    private RequestType requestType;
    private LocalDateTime modifiedFrom;

    public boolean isEmpty() {
        return vacancies.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
