package vacancy_tracker.sources.superjob.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vacancy_tracker.sources.superjob.model.dto.SuperJobTownDto;

import java.util.List;

@Data
public class SuperJobCitiesResponse {

    @JsonProperty("objects")
    List<SuperJobTownDto> cities;

    Integer total;
}
