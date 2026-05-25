package vacancy_tracker.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.model.telegram.callback.CallbackItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CallbackItemMapper {

    String REGION_PREFIX = "select_region";
    String TOWN_PREFIX = "select_town";

    @Mapping(target = "key", source = "id", qualifiedByName = "toStringValue")
    @Mapping(target = "callbackPrefix", constant = REGION_PREFIX)
    @Mapping(target = "displayedName", source = "name")
    CallbackItem fromRegion(Region region);

    @Mapping(target = "key", source = "id", qualifiedByName = "toStringValue")
    @Mapping(target = "callbackPrefix", constant = TOWN_PREFIX)
    @Mapping(target = "displayedName", source = "name")
    CallbackItem fromTown(Town town);

    List<CallbackItem> fromRegions(List<Region> regions);

    List<CallbackItem> fromTowns(List<Town> towns);

    @Named("toStringValue")
    default String toStringValue(Object value) {
        return String.valueOf(value);
    }
}
