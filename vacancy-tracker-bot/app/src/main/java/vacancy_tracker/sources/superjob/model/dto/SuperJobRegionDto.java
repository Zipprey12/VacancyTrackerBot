package vacancy_tracker.sources.superjob.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SuperJobRegionDto {

    private Integer id;

    @JsonProperty("id_country")
    private Integer countryId;

    @JsonProperty("title")
    private String name;
}
