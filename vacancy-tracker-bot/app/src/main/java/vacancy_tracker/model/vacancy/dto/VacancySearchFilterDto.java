package vacancy_tracker.model.vacancy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancySearchFilterDto {

    private Integer offset;
    private Integer limit;

    private String text;
    private LocationDto region;
    private String modifiedFrom;
    private Integer minSalary;
    private Integer maxSalary;
}
