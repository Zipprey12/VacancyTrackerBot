package vacancy_tracker.model.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("company_name")
    private Company company;

    @JsonProperty("salary_min")
    private Integer salaryMin;

    @JsonProperty("salary_max")
    private Integer salaryMax;

    private Location location;

    @JsonProperty("vacancy_url")
    private String vacancyUrl;

    @JsonProperty("creation_date")
    private String creationDate;

    @JsonProperty("employment_type")
    private String employmentType;

    @JsonProperty("schedule_type")
    private String scheduleType;
}
