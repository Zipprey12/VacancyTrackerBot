package vacancy_tracker.sources.superjob.service.vacancy;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vacancy_tracker.model.domain.Company;
import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.domain.Town;
import vacancy_tracker.model.domain.Vacancy;
import vacancy_tracker.sources.superjob.model.dto.SuperJobTownDto;
import vacancy_tracker.sources.superjob.model.dto.SuperJobVacancyDto;

@org.mapstruct.Mapper(componentModel = "spring")
public interface SuperJobVacancyMapper {

    @Mapping(source = "town", target = "location", qualifiedByName = "townToLocation")
    @Mapping(source = "companyId", target = "company", qualifiedByName = "companyIdToCompany")
    Vacancy toEntity(SuperJobVacancyDto dto);

    @Mapping(source = "location", target = "town", qualifiedByName = "locationToTown")
    @Mapping(source = "company", target = "companyId", qualifiedByName = "companyToCompanyId")
    SuperJobVacancyDto toDto(Vacancy vacancy);

    @Named("townToLocation")
    default Location townToLocation(SuperJobTownDto townDto) {
        if (townDto == null) {
            return null;
        }
        var town = Town.builder()
                .name(townDto.getName())
                .id(townDto.getId())
                .build();

        var location = new Location();
        location.setTown(town);
        return location;
    }

    @Named("locationToTown")
    default SuperJobTownDto locationToTown(Location location) {
        if (location == null) {
            return null;
        }
        var town = location.getTown();
        if (town == null) {
            return null;
        }

        return SuperJobTownDto.builder()
                .id(town.getId())
                .name(town.getName())
                .build();
    }

    @Named("companyIdToCompany")
    default Company companyIdToCompany(int id) {
        return Company.builder()
                .id(id)
                .build();
    }

    @Named("companyToCompanyId")
    default int companyToCompanyId(Company company) {
        return company.getId();
    }
}