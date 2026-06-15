package vacancy_tracker.model.domain;


import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Company {
    private Integer id;
    private String name;
    private String url;
    private Timestamp lastUpdateAt;

    public String toString() {
        return "Company(id=" + id + " name=" + name + ")";
    }
}
