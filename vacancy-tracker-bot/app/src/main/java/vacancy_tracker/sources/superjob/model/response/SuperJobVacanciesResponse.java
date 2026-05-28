package vacancy_tracker.sources.superjob.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import vacancy_tracker.sources.superjob.model.dto.SuperJobVacancyDto;

import java.util.List;

@Getter
public class SuperJobVacanciesResponse {

    @JsonProperty("objects")
    private List<SuperJobVacancyDto> vacancies;

    private Integer total;
    private Boolean more;

    @Setter
    private Integer offset;

    @Setter
    private Integer page;

    public List<SuperJobVacancyDto> getVacanciesSafe() {
        return vacancies != null ? vacancies : List.of();
    }
}
