package vacancy_tracker.sources.trudvsem.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vacancy_tracker.model.api.Company;
import vacancy_tracker.model.api.ExtendedRegion;
import vacancy_tracker.model.api.Location;
import vacancy_tracker.model.api.Vacancy;
import vacancy_tracker.sources.trudvsem.model.dto.TrudVsemVacancyDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrudVsemVacancyMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "salaryMin", source = "salaryMin")
    @Mapping(target = "salaryMax", source = "salaryMax")
    @Mapping(target = "vacancyUrl", source = "vacancyUrl")
    @Mapping(target = "creationDate", source = "creationDate")
    @Mapping(target = "company", expression = "java(toCompany(dto))")
    @Mapping(target = "location", expression = "java(toLocation(dto))")
    Vacancy toEntity(TrudVsemVacancyDto dto);

    List<Vacancy> toEntityList(List<TrudVsemVacancyDto> dtoList);

    default Company toCompany(TrudVsemVacancyDto dto) {
        if (dto == null || dto.getCompany() == null) {
            return null;
        }
        return Company.builder()
                .name(dto.getCompany().getName())
                .url(dto.getCompany().getUrl())
                .build();
    }

    default Location toLocation(TrudVsemVacancyDto dto) {
        if (dto == null || dto.getRegion() == null) {
            return null;
        }
        return Location.builder()
                .region(ExtendedRegion.builder()
                        .name(dto.getRegion().getName())
                        .build())
                .build();
    }
}
