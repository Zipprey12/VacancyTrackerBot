package vacancy_tracker.model.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    private ExtendedRegion region;
    private Town town;

    @Override
    public String toString() {
        return "Location (region: " + (region == null ? null : region.getName()) + ", "
                + "town: " + (town == null ? null : town.getName()) + ")";
    }
}
