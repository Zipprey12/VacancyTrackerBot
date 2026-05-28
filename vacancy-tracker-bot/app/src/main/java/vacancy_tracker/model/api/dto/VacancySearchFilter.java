package vacancy_tracker.model.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.model.api.Location;
import vacancy_tracker.model.api.RequestType;

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

    private LocalDateTime modifiedFrom;
    private RequestType requestType;
}
