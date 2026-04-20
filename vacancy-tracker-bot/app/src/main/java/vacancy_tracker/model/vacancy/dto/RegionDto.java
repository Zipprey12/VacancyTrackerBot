package vacancy_tracker.model.vacancy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegionDto {
    private Integer id;

    @JsonProperty("id_country")
    private Integer countryId;

    @JsonProperty("title")
    private String name;
}
