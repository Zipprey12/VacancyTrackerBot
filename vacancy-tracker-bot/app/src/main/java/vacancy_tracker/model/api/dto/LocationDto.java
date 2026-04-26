package vacancy_tracker.model.api.dto;

import lombok.Data;
import vacancy_tracker.sources.superjob.model.dto.SuperJobTownDto;

@Data
public class LocationDto {
    private SuperJobTownDto city;
    private RegionDto region;
}
