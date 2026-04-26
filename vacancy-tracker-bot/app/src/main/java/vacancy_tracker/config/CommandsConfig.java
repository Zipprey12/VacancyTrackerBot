package vacancy_tracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vacancy_tracker.services.telegram.command.ForceSearchVacanciesCommand;
import vacancy_tracker.services.telegram.command.HelpCommand;
import vacancy_tracker.services.telegram.command.InitCommand;
import vacancy_tracker.services.telegram.command.MessageBotCommand;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommandFactory;
import vacancy_tracker.services.telegram.command.settings.SetSearchSettingsCommand;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.FiltersMessageFormatter;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.VacanciesMessageFormatter;
import vacancy_tracker.sources.superjob.service.vacancy.SuperJobVacanciesService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CommandsConfig {

    private final ApplicationEventPublisher publisher;
    private final MessageSender messageSender;
    private final SettingsService settingsService;
    private final SessionsService sessionsService;
    private final VacanciesMessageFormatter vacanciesFormatter;
    private final SuperJobVacanciesService jobVacanciesService;
    private final FiltersMessageFormatter filtersMessageFormatter;
    private final KeyboardBuilder keyboardBuilder;
    private final SearchFiltersCommandFactory searchFiltersCommandFactory;

    @Bean
    public InitCommand initCommand() {
        return new InitCommand(messageSender);
    }

    @Bean
    public ForceSearchVacanciesCommand forceSearchCommand() {
        return new ForceSearchVacanciesCommand(messageSender,
                settingsService,
                jobVacanciesService,
                vacanciesFormatter);
    }

    @Bean
    public SetSearchSettingsCommand setSearchSettingsCommand() {
        return new SetSearchSettingsCommand(messageSender,
                settingsService,
                filtersMessageFormatter,
                keyboardBuilder);
    }

    @Bean
    public SearchFiltersCommand setMaxSalaryCommand() {
        return searchFiltersCommandFactory.createMaxSalaryCommand();
    }

    @Bean
    public SearchFiltersCommand setMinSalaryCommand() {
        return searchFiltersCommandFactory.createMinSalaryCommand();
    }

    @Bean
    public SearchFiltersCommand setSearchingTextCommand() {
        return searchFiltersCommandFactory.createSearchingTextCommand();
    }

    @Bean(name = "allCommands")
    public List<MessageBotCommand> commands(ForceSearchVacanciesCommand forceSearchCommand,
                                            SetSearchSettingsCommand setSearchSettingsCommand) {
        return List.of(
                forceSearchCommand,
                setSearchSettingsCommand
        );
    }

    @Bean
    public HelpCommand helpCommand(@Qualifier("allCommands") List<MessageBotCommand> allCommands) {
        return new HelpCommand(messageSender, allCommands);
    }
}
