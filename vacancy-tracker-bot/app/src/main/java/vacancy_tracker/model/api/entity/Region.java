package vacancy_tracker.model.api.entity;

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
    private int id;
    private String name;
    private List<Town> towns;

    public void addCity(Town city) {
        if (towns == null) {
            towns = new LinkedList<>();
        }
        towns.add(city);
    }
}
