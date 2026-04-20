package vacancy_tracker.model.vacancy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetaDto {
    private Integer total;
    private Integer limit;
    private Integer offset;
}
