package vacancy_tracker.model.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Town {
    private int id;
    private int regionId;
    private String name;
}
