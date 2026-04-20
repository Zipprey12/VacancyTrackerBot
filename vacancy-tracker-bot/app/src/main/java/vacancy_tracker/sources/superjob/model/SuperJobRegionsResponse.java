package vacancy_tracker.sources.superjob.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vacancy_tracker.model.vacancy.dto.RegionDto;

import java.util.List;

@Data
public class SuperJobRegionsResponse {

    @JsonProperty("objects")
    List<RegionDto> regions;

    Boolean more;

    Integer total;
}
