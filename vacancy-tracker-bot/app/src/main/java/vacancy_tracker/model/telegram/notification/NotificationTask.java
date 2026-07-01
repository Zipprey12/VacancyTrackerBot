package vacancy_tracker.model.telegram.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.model.domain.Vacancy;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTask {

    private Long sessionId;

    private List<Vacancy> vacancies;

    private boolean emptyResult;
}
