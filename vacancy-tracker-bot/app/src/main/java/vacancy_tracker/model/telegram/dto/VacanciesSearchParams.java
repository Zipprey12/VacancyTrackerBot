package vacancy_tracker.model.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.model.api.RequestType;
import vacancy_tracker.model.api.VacanciesSource;

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
