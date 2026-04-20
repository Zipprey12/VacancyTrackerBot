package vacancy_tracker.model.vacancy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CityDto {
    Integer id;

    @JsonProperty("id_region")
    Integer regionId;

    @JsonProperty("title")
    String name;
}
