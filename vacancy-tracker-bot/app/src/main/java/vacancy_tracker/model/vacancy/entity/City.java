package vacancy_tracker.model.vacancy.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class City {
    int id;
    int regionId;
    String name;
}
