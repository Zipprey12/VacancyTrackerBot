package vacancy_tracker.model.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.domain.RequestType;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancySearchFilter {

    private String text;
    private Integer minSalary;
    private Integer maxSalary;
    private Location location;
    private Float experience;

    private Instant updatedAt;

    private LocalDateTime modifiedFrom;
    private RequestType requestType;
}
