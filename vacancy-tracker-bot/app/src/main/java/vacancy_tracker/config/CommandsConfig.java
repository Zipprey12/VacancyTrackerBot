package vacancy_tracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.services.telegram.command.MessageBotCommand;
import vacancy_tracker.services.telegram.command.interceptors.SearchTextInterceptor;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;
import vacancy_tracker.services.telegram.command.settings.SetRegionCommand;
import vacancy_tracker.services.telegram.command.settings.SetSearchSettingsCommand;
import vacancy_tracker.services.telegram.command.settings.SetSearchingTextCommand;
import vacancy_tracker.services.telegram.command.simple.ForceSearchVacanciesCommand;
import vacancy_tracker.services.telegram.command.simple.HelpCommand;
import vacancy_tracker.services.telegram.command.simple.InitCommand;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.VacanciesMessageFormatter;
import vacancy_tracker.sources.superjob.service.vacancy.SuperJobVacanciesService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@DependsOn("initializer")
public class CommandsConfig {

    private final ApplicationEventPublisher publisher;
    private final MessageSender messageSender;
    private final SettingsService settingsService;
    private final VacanciesMessageFormatter vacanciesFormatter;
    private final SuperJobVacanciesService jobVacanciesService;
    private final SessionsService sessionsService;
    private final MessageEditor messageEditor;

    @Bean
    public InitCommand initCommand() {
        return new InitCommand(messageSender, messageEditor);
    }

    @Bean
    public ForceSearchVacanciesCommand forceSearchCommand() {
        return new ForceSearchVacanciesCommand(messageSender,
                messageEditor,
                settingsService,
                jobVacanciesService,
                vacanciesFormatter);
    }

    @Bean
    public SearchFiltersCommand setSearchingTextCommand() {
        return new SetSearchingTextCommand(messageSender,
                messageEditor,
                sessionsService,
                new SearchTextInterceptor(messageSender, sessionsService, settingsService),
                publisher,
                settingsService);
    }

    @Bean(name = "allCommands")
    public List<MessageBotCommand> commands(ForceSearchVacanciesCommand forceSearchCommand,
                                            SetSearchSettingsCommand setSearchSettingsCommand,
                                            SetRegionCommand setLocationCommand) {
        return List.of(
                forceSearchCommand,
                setSearchSettingsCommand,
                setLocationCommand
        );
    }

    @Bean
    public HelpCommand helpCommand(@Qualifier("allCommands") List<MessageBotCommand> allCommands) {
        return new HelpCommand(messageSender, messageEditor, allCommands);
    }
}
