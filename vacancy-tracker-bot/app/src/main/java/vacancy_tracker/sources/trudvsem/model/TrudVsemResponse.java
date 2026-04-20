package vacancy_tracker.sources.trudvsem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vacancy_tracker.model.vacancy.dto.MetaDto;
import vacancy_tracker.model.vacancy.Vacancy;

import java.util.List;

@Data
public class TrudVsemResponse {

    @JsonProperty("results")
    private List<Vacancy> vacancies;

    private MetaDto meta;
}
