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

    public void setMaxSalary(Integer maxSalary) {
        if (maxSalary == null || maxSalary == 0) {
            this.maxSalary = null;
            return;
        }
        this.maxSalary = maxSalary;
    }

    public void setMinSalary(Integer minSalary) {
        if (minSalary == null || minSalary == 0) {
            this.minSalary = null;
            return;
        }
        this.minSalary = minSalary;
    }
}
