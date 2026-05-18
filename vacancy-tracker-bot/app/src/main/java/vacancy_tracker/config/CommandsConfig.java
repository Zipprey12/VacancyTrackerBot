package vacancy_tracker.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingMessagePublisher;
import vacancy_tracker.services.telegram.command.settings.notification.SetNotificationSettingsCommand;
import vacancy_tracker.services.telegram.command.settings.search.SetRegionCommand;
import vacancy_tracker.services.telegram.command.settings.search.SetSearchSettingsCommand;
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
    public List<CompletableMessageCommand> commands(ForceSearchVacanciesCommand forceSearchCommand,
                                                    SetSearchSettingsCommand setSearchSettingsCommand,
                                                    SetRegionCommand setLocationCommand,
                                                    SetNotificationSettingsCommand setNotificationSettingsCommand) {
        return List.of(
                forceSearchCommand,
                setSearchSettingsCommand,
                setLocationCommand,
                setNotificationSettingsCommand
        );
    }

    @Bean
    public HelpCommand helpCommand(SendingMessagePublisher publisher,
                                   @Qualifier("allCommands") List<CompletableMessageCommand> allCommands) {
        return new HelpCommand(publisher, allCommands);
    }

    @PostConstruct
    public void init() {
        var regions = locationsService.getAllRegionsBasic();
        regionsSelectionMessageFormatter.setRegions(regions);
    }
}
