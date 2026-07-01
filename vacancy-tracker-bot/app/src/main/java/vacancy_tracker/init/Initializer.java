package vacancy_tracker.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import vacancy_tracker.services.api.location.LocationsService;

@Component
@RequiredArgsConstructor
public class Initializer {

    private final LocationsService locationsService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        locationsService.initialize();
    }
}