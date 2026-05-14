package vacancy_tracker.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.services.vacancy.LocationsService;

@Component
@RequiredArgsConstructor
public class Initializer {

    private final LocationsService locationsService;

    @PostConstruct
    public void init() {
        locationsService.initialize();
    }
}
