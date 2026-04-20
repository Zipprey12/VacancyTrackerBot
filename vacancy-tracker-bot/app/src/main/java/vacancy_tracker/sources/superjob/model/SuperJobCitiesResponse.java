package vacancy_tracker.sources.superjob.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vacancy_tracker.model.vacancy.dto.CityDto;

import java.util.List;

@Data
public class SuperJobCitiesResponse {

    @JsonProperty("objects")
    List<CityDto> cities;

    Integer total;
}
