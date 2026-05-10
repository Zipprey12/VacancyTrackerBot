package vacancy_tracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.MessageBotCommand;
import vacancy_tracker.services.telegram.command.interceptors.*;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;
import vacancy_tracker.services.telegram.command.settings.SetRegionCommand;
import vacancy_tracker.services.telegram.command.settings.SetSearchSettingsCommand;
import vacancy_tracker.services.telegram.command.settings.SetTownCommand;
import vacancy_tracker.services.telegram.command.simple.ForceSearchVacanciesCommand;
import vacancy_tracker.services.telegram.command.simple.HelpCommand;
import vacancy_tracker.services.telegram.command.simple.InitCommand;
import vacancy_tracker.services.telegram.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.FiltersMessageFormatter;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;
import vacancy_tracker.services.telegram.view.VacanciesMessageFormatter;
import vacancy_tracker.services.vacancy.LocationsService;
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
    private final FiltersMessageFormatter filtersMessageFormatter;
    private final KeyboardBuilder keyboardBuilder;
    private final SessionsService sessionsService;
    private final MessageEditor messageEditor;
    private final LocationsService locationsService;
    private final CallbackItemMapper callbackItemMapper;

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
    public SetSearchSettingsCommand setSearchSettingsCommand() {
        return new SetSearchSettingsCommand(messageSender,
                messageEditor,
                settingsService,
                filtersMessageFormatter,
                keyboardBuilder);
    }

    @Bean
    public SearchFiltersCommand setMaxSalaryCommand() {
        return new SearchFiltersCommand("/set_max_salary",
                "Установить максимальное значение зарплаты",
                messageSender,
                messageEditor,
                sessionsService,
                new MaxSalaryInterceptor(messageSender, sessionsService, settingsService),
                publisher) {

            @Override
            protected void executeAndPopulateMessage(OutgoingMessage messageData) {
                messageData.setText("Укажите максимальное значение зарплаты:");
            }
        };
    }

    @Bean
    public SearchFiltersCommand setMinSalaryCommand() {
        return new SearchFiltersCommand("/set_min_salary",
                "Установить минимальное значение зарплаты",
                messageSender,
                messageEditor,
                sessionsService,
                new MinSalaryInterceptor(messageSender, sessionsService, settingsService),
                publisher) {

            @Override
            protected void executeAndPopulateMessage(OutgoingMessage messageData) {
                messageData.setText("Укажите минимальное значение зарплаты:");
            }
        };
    }

    @Bean
    public SearchFiltersCommand setSearchingTextCommand() {
        return new SearchFiltersCommand("/set_search_text",
                "Установить текст для поиска:",
                messageSender,
                messageEditor,
                sessionsService,
                new SearchTextInterceptor(messageSender, sessionsService, settingsService),
                publisher) {

            @Override
            protected void executeAndPopulateMessage(OutgoingMessage messageData) {
                messageData.setText("Укажите текст для поиска: ");
            }
        };
    }

    @Bean
    public SearchFiltersCommand setExperienceCommand() {
        return new SearchFiltersCommand("/set_experience",
                "Установить минимальный опыт работы",
                messageSender,
                messageEditor,
                sessionsService,
                new ExperienceInterceptor(messageSender, sessionsService, settingsService),
                publisher) {
            @Override
            protected void executeAndPopulateMessage(OutgoingMessage messageData) {
                messageData.setText("Укажите минимальный опыт в годах:");
            }
        };
    }

    @Bean
    public SetRegionCommand setLocationCommand(PaginatedKeyboardBuilder regionsPaginationBuilder,
                                               SetRegionInterceptor setRegionInterceptor) {
        return new SetRegionCommand(
                messageSender,
                messageEditor,
                sessionsService,
                publisher,
                regionsPaginationBuilder,
                setRegionInterceptor
        );
    }

    @Bean
    public SetTownCommand setTownCommand(PaginatedKeyboardBuilder townsPaginationBuilder,
                                         SetTownInterceptor setTownInterceptor) {
        return new SetTownCommand(
                messageSender,
                messageEditor,
                sessionsService,
                setTownInterceptor,
                publisher,
                settingsService,
                townsPaginationBuilder,
                locationsService,
                callbackItemMapper
        );
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
