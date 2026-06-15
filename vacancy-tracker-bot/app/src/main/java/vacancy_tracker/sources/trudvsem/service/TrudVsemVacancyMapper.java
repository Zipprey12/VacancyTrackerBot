package vacancy_tracker.sources.trudvsem.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vacancy_tracker.model.domain.Company;
import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.domain.Region;
import vacancy_tracker.model.domain.Vacancy;
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
                .region(Region.builder()
                        .name(dto.getRegion().getName())
                        .build())
                .build();
    }
}
