package vacancy_tracker.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.services.telegram.command.MessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingMessagePublisher;
import vacancy_tracker.services.telegram.command.settings.SetRegionCommand;
import vacancy_tracker.services.telegram.command.settings.SetSearchSettingsCommand;
import vacancy_tracker.services.telegram.command.simple.ForceSearchVacanciesCommand;
import vacancy_tracker.services.telegram.command.simple.HelpCommand;
import vacancy_tracker.services.telegram.view.formatters.RegionsSelectionMessageFormatter;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@DependsOn("initializer")
public class CommandsConfig {

    private final LocationsService locationsService;
    private final RegionsSelectionMessageFormatter regionsSelectionMessageFormatter;

    @Bean(name = "allCommands")
    public List<MessageCommand> commands(ForceSearchVacanciesCommand forceSearchCommand,
                                         SetSearchSettingsCommand setSearchSettingsCommand,
                                         SetRegionCommand setLocationCommand) {
        return List.of(
                forceSearchCommand,
                setSearchSettingsCommand,
                setLocationCommand
        );
    }

    @Bean
    public HelpCommand helpCommand(SendingMessagePublisher publisher,
                                   @Qualifier("allCommands") List<MessageCommand> allCommands) {
        return new HelpCommand(publisher, allCommands);
    }

    @PostConstruct
    public void init() {
        var regions = locationsService.getAllRegionsBasic();
        regionsSelectionMessageFormatter.setRegions(regions);
    }
}
