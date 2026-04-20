package vacancy_tracker.sources.superjob.service.vacancy;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vacancy_tracker.model.vacancy.Vacancy;
import vacancy_tracker.model.vacancy.dto.CityDto;
import vacancy_tracker.model.vacancy.entity.Company;
import vacancy_tracker.sources.superjob.model.SuperJobVacancyDto;

@org.mapstruct.Mapper(componentModel = "spring")
public interface SuperJobVacancyMapper {

    @Mapping(source = "city", target = "regionName", qualifiedByName = "cityToRegionName")
    @Mapping(source = "companyId", target = "company", qualifiedByName = "companyIdToCompany")
    Vacancy toEntity(SuperJobVacancyDto dto);

    @Mapping(source = "regionName", target = "city", qualifiedByName = "regionNameToCity")
    @Mapping(source = "company", target = "companyId", qualifiedByName = "companyToCompanyId")
    SuperJobVacancyDto toDto(Vacancy vacancy);

    @Named("cityToRegionName")
    default String townToRegionName(CityDto city) {
        if (city == null) {
            return null;
        }
        return city.getName();
    }

    @Named("regionNameToCity")
    default CityDto regionNameToTown(String regionName) {
        if (regionName == null) {
            return null;
        }
        CityDto city = new CityDto();
        city.setName(regionName);
        return city;
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