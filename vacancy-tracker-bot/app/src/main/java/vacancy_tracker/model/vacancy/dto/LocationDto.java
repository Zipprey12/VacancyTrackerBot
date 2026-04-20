package vacancy_tracker.model.vacancy.dto;

import lombok.Data;

@Data
public class LocationDto {
    private CityDto city;
    private RegionDto region;
}
