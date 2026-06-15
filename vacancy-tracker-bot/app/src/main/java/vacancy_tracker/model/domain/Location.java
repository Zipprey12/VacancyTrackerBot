package vacancy_tracker.model.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    private Region region;
    private Town town;

    @Override
    public String toString() {
        return "Location (region: " + (region == null ? null : region.getName()) + ", "
                + "town: " + (town == null ? null : town.getName()) + ")";
    }
}
