package vacancy_tracker.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import vacancy_tracker.model.api.Location;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.entities.SearchFilterEntity;
import vacancy_tracker.services.api.location.LocationsService;

@Mapper(componentModel = "spring")
public abstract class SearchFilterMapper {

    @Autowired
    protected LocationsService locationsService;

    @Mapping(target = "location", expression = "java(toLocation(entity))")
    @Mapping(target = "requestType", ignore = true)
    public abstract VacancySearchFilter toDto(SearchFilterEntity entity);

    @Mapping(target = "regionId", expression = "java(toRegionId(filter))")
    @Mapping(target = "townId", expression = "java(toTownId(filter))")
    @Mapping(target = "chatId", ignore = true)
    public abstract void updateEntity(VacancySearchFilter filter, @MappingTarget SearchFilterEntity entity);

    protected Location toLocation(SearchFilterEntity entity) {
        if (entity.getRegionId() == null && entity.getTownId() == null) {
            return null;
        }
        if (entity.getTownId() != null) {
            return locationsService.getLocationByTownId(entity.getTownId()).orElse(null);
        } else {
            return locationsService.getLocationByRegionCode(entity.getRegionId()).orElse(null);
        }
    }

    protected Integer toRegionId(VacancySearchFilter filter) {
        if (filter.getLocation() == null || filter.getLocation().getRegion() == null) {
            return null;
        }
        return filter.getLocation().getRegion().getCode();
    }

    protected Integer toTownId(VacancySearchFilter filter) {
        if (filter.getLocation() == null || filter.getLocation().getTown() == null) {
            return null;
        }
        return filter.getLocation().getTown().getId();
    }
}