package vacancy_tracker.model.persistence;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "regions")
@Getter
@Setter
public class RegionEntity {

    @Id
    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "name")
    private String name;

}
