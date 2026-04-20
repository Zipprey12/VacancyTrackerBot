package vacancy_tracker.sources.superjob.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vacancy_tracker.model.vacancy.dto.CityDto;

@Data
public class SuperJobVacancyDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("profession")
    private String name;

    @JsonProperty("id_client")
    private Integer companyId;

    @JsonProperty("payment_from")
    private Integer salaryMin;

    @JsonProperty("payment_to")
    private Integer salaryMax;

    @JsonProperty("town")
    private CityDto city;

    @JsonProperty("link")
    private String vacancyUrl;

    @JsonProperty("date_published")
    private String creationDate;


    @JsonProperty("employment_type")
    private String employmentType;

    @JsonProperty("schedule_type")
    private String scheduleType;

}
