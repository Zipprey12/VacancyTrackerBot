package vacancy_tracker.sources.trudvsem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.model.api.dto.MetaDto;
import vacancy_tracker.sources.trudvsem.model.dto.TrudVsemVacancyDto;

import java.util.List;

@Data
public class TrudVsemResponse {

    @JsonProperty("results")
    private Results results;

    private MetaDto meta;

    public List<TrudVsemVacancyDto> getVacanciesSafe() {
        if (results == null || results.getVacancies() == null) {
            return List.of();
        }
        return results.getVacancies().stream()
                .map(VacancyWrapper::getVacancy)
                .toList();
    }

    @Data
    @NoArgsConstructor
    public static class Results {
        @JsonProperty("vacancies")
        private List<VacancyWrapper> vacancies;
    }

    @Data
    @NoArgsConstructor
    public static class VacancyWrapper {
        @JsonProperty("vacancy")
        private TrudVsemVacancyDto vacancy;
    }
}
