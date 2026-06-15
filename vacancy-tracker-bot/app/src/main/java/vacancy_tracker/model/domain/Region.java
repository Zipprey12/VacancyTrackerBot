package vacancy_tracker.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    private int code;
    private String name;
    private List<Town> towns;

    public void addCity(Town city) {
        if (towns == null) {
            towns = new LinkedList<>();
        }
        towns.add(city);
    }
}
