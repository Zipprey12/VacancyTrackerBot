package vacancy_tracker.sources.trudvsem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vacancy_tracker.model.api.dto.MetaDto;
import vacancy_tracker.model.api.entity.Vacancy;

import java.util.List;

@Data
public class TrudVsemResponse {

    @JsonProperty("results")
    private List<Vacancy> vacancies;

    private MetaDto meta;
}
