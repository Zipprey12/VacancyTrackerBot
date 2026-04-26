package vacancy_tracker.model.api.entity;


import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Company {
    private Integer id;
    private String name;
    private String link;
    private Timestamp lastUpdateAt;
}
