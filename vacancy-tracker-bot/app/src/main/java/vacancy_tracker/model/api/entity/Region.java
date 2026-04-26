package vacancy_tracker.model.api.entity;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
public class Region {
    private int id;
    private String name;
    private List<Town> cities;

    public void addCity(Town city){
        if(cities == null){
            cities = new LinkedList<>();
        }
        cities.add(city);
    }
}
