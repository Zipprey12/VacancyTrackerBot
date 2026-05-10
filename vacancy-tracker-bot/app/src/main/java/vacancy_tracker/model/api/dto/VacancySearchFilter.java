package vacancy_tracker.model.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.model.api.entity.Location;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancySearchFilter {
    private Integer offset;
    private Integer limit;

    private String text;
    private Integer minSalary;
    private Integer maxSalary;
    private Location location;
    private Float experienceFrom;

    private String modifiedFrom;
}
