package vacancy_tracker.model.vacancy.entity;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
public class Region {
    int id;
    String name;
    List<City> cities;

    public void addCity(City city){
        if(cities == null){
            cities = new LinkedList<>();
        }
        cities.add(city);
    }
}
