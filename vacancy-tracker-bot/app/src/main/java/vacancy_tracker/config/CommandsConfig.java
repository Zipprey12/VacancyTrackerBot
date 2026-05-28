package vacancy_tracker.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.services.api.location.LocationsService;
import vacancy_tracker.services.telegram.view.formatters.filter.RegionsSelectionMessageFormatter;

@Configuration
@RequiredArgsConstructor
@DependsOn("initializer")
public class CommandsConfig {

    private final LocationsService locationsService;
    private final RegionsSelectionMessageFormatter regionsSelectionMessageFormatter;

    @PostConstruct
    public void init() {
        var regions = locationsService.getAllRegionsBasic();
        regionsSelectionMessageFormatter.setRegions(regions);
    }
}
