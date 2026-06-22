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

    private long page;
    private long offset;

    private boolean more;
    private long total;
    private boolean isSuccess;

    private boolean canHasOther;

    private RequestType requestType;
    private LocalDateTime modifiedFrom;

    public boolean isCanHasOther() {
        return canHasOther || isNotEmpty();
    }

    public boolean canBeFilled() {
        return isNotEmpty() || more || total > 0 || canHasOther;
    }

    public boolean isEmpty() {
        return vacancies.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
