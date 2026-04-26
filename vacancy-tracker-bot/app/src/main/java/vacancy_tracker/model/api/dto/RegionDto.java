package vacancy_tracker.model.api.dto;

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
