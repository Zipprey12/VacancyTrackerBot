package vacancy_tracker.sources.trudvsem.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrudVsemVacancyDto {
    private String id;
    private String name;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("salary_min")
    private Integer salaryMin;

    @JsonProperty("salary_max")
    private Integer salaryMax;

    @JsonProperty("region_name")
    private String regionName;

    @JsonProperty("vacancy_url")
    private String vacancyUrl;

    @JsonProperty("creation_date")
    private String creationDate;

    @JsonProperty("employment_type")
    private String employmentType;

    @JsonProperty("schedule_type")
    private String scheduleType;
}
