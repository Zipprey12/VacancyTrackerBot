package vacancy_tracker.model.search;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.model.domain.RequestType;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.domain.Vacancy;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
