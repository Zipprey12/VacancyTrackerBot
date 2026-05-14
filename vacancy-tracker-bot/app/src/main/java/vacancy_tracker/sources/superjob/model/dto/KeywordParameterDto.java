package vacancy_tracker.sources.superjob.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KeywordParameterDto {

    @JsonProperty("srws")
    private static final int  TYPE_OF_PLACE = 1;

    @JsonProperty("skwc")
    private static final String TYPE_OF_SEARCH = "or";

    private final String key;
}
