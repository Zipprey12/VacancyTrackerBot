package vacancy_tracker.services.telegram.command.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.MessageData;
import vacancy_tracker.services.telegram.command.interceptors.MaxSalaryInterceptor;
import vacancy_tracker.services.telegram.command.interceptors.MinSalaryInterceptor;
import vacancy_tracker.services.telegram.command.interceptors.SearchTextInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

@Component
@RequiredArgsConstructor
public class SearchFiltersCommandFactory {

    private final MessageSender sender;
    private final SettingsService settingsService;
    private final SessionsService sessionsService;
    private final ApplicationEventPublisher eventPublisher;

    public SearchFiltersCommand createMaxSalaryCommand() {

        return new SearchFiltersCommand("/set_max_salary",
                "Установить максимальное значение зарплаты",
                sender,
                sessionsService,
                new MaxSalaryInterceptor(sender, sessionsService, settingsService),
                eventPublisher) {

            @Override
            protected void handle(MessageData messageData) {
                sender.sendText(messageData.getChatId(), "Укажите максимальное значение зарплаты:");
            }
        };
    }

    public SearchFiltersCommand createMinSalaryCommand() {
        return new SearchFiltersCommand("/set_max_salary",
                "Установить минимальное значение зарплаты",
                sender,
                sessionsService,
                new MinSalaryInterceptor(sender, sessionsService, settingsService),
                eventPublisher) {

            @Override
            protected void handle(MessageData messageData) {
                sender.sendText(messageData.getChatId(), "Укажите минимальное значение зарплаты:");
            }
        };
    }

    public SearchFiltersCommand createSearchingTextCommand() {
        return new SearchFiltersCommand("/set_search_text",
                "Установить текст для поиска:",
                sender,
                sessionsService,
                new SearchTextInterceptor(sender, sessionsService, settingsService), eventPublisher) {

            @Override
            protected void handle(MessageData messageData) {
                sender.sendText(messageData.getChatId(), "Укажите текст для поиска:");
            }
        };
    }

}
