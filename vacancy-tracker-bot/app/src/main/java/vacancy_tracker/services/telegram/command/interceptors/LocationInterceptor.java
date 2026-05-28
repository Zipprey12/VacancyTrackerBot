package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.model.telegram.dto.LocationSearch;

import java.util.Optional;

public class LocationInterceptor extends InputInterceptor<LocationSearch> {

    @Override
    protected Optional<LocationSearch> tryCastPreparedInput(String text) {
        return LocationSearch.parse(text);
    }
}
