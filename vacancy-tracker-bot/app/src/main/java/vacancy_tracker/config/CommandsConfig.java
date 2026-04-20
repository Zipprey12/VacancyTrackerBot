package vacancy_tracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vacancy_tracker.services.telegram.command.ForceSearchVacanciesCommand;
import vacancy_tracker.services.telegram.command.HelpCommand;
import vacancy_tracker.services.telegram.command.InitCommand;
import vacancy_tracker.services.telegram.command.MessageBotCommand;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.VacanciesMessageFormatter;
import vacancy_tracker.sources.superjob.service.vacancy.SuperJobVacanciesService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CommandsConfig {

    private final MessageSender messageSender;
    private final SettingsService settingsService;
    private final VacanciesMessageFormatter vacanciesFormatter;
    private final SuperJobVacanciesService jobVacanciesService;

    @Bean
    public MessageBotCommand initCommand() {
        return new InitCommand(messageSender);
    }

    @Bean
    public MessageBotCommand forceSearchCommand() {
        return new ForceSearchVacanciesCommand(messageSender,
                settingsService,
                jobVacanciesService,
                vacanciesFormatter);
    }

    @Bean
    public MessageBotCommand helpCommand() {
        return new HelpCommand(messageSender, commands());
    }

    @Bean
    public List<MessageBotCommand> commands() {
        return List.of(forceSearchCommand());
    }
}
