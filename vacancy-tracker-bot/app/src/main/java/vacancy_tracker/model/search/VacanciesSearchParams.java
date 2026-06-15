package vacancy_tracker.model.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.model.domain.RequestType;
import vacancy_tracker.model.domain.VacanciesSource;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacanciesSearchParams {

    private int page;
    private VacanciesSource source;
    private LocalDateTime startDate;
    private RequestType requestType;
}
