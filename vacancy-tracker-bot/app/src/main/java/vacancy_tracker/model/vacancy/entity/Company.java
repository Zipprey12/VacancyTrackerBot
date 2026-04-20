package vacancy_tracker.model.vacancy.entity;


import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Company {
    Integer id;
    String name;
    String link;
    Timestamp lastUpdateAt;
}
