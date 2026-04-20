package vacancy_tracker.sources.superjob.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import vacancy_tracker.model.vacancy.dto.MetaDto;

import java.util.List;

public class SuperJobVacanciesResponse {

    @JsonProperty("objects")
    private List<SuperJobVacancyDto> vacancies;

    private Integer total;
    private Boolean more;

    public List<SuperJobVacancyDto> getVacanciesSafe() {
        return vacancies != null ? vacancies : List.of();
    }

    public boolean hasVacancies() {
        return vacancies != null && !vacancies.isEmpty();
    }

    public MetaDto toMetaDto(Integer limit, Integer offset) {
        return MetaDto.builder()
                .total(total)
                .limit(limit)
                .offset(offset)
                .build();
    }
}
