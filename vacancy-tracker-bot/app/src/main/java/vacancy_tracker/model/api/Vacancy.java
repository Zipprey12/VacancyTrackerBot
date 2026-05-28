package vacancy_tracker.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy {

    private String id;
    private String name;

    private Company company;

    private Integer salaryMin;

    private Integer salaryMax;

    private Location location;

    private String vacancyUrl;

    private String creationDate;
}
