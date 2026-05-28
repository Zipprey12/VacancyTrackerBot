package vacancy_tracker.sources.superjob.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vacancy_tracker.sources.superjob.model.dto.SuperJobRegionDto;

import java.util.List;

@Data
public class SuperJobRegionsResponse {

    @JsonProperty("objects")
    List<SuperJobRegionDto> regions;

    Boolean more;

    Integer total;
}
