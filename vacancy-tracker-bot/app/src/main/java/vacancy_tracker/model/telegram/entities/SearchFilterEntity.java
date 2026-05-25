package vacancy_tracker.model.telegram.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "search_filters")
@Getter
@Setter
@NoArgsConstructor
public class SearchFilterEntity {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    // @OneToOne и @MapsId — убрать полностью

    @Column(name = "text")
    private String text;

    @Column(name = "min_salary")
    private Integer minSalary;

    @Column(name = "max_salary")
    private Integer maxSalary;

    @Column(name = "experience")
    private Float experience;

    @Column(name = "modified_from")
    private String modifiedFrom;

    @Column(name = "region_id")
    private Integer regionId;

    @Column(name = "town_id")
    private Integer townId;

    public SearchFilterEntity(long chatId) {
        this.chatId = chatId;
    }
}