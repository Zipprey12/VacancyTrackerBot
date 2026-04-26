package vacancy_tracker.sources.superjob.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperJobTownDto {
    Integer id;

    @JsonProperty("title")
    String name;
}
