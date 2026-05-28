package vacancy_tracker.sources.trudvsem.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TrudVsemVacancyDto {
    private String id;

    @JsonProperty("job-name")
    private String name;

    @JsonProperty("creation-date")
    private String creationDate;

    @JsonProperty("salary_min")
    private Integer salaryMin;

    @JsonProperty("salary_max")
    private Integer salaryMax;

    @JsonProperty("vac_url")
    private String vacancyUrl;

    private TrudVsemCompanyDto company;

    private TrudVsemRegionDto region;
}
