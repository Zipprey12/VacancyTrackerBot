package vacancy_tracker.services.telegram.command.settings;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.interceptors.SearchTextInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class SetSearchTextCommand extends InputInterceptingCommand {

    protected SetSearchTextCommand(MessageSender sender, SessionsService sessionsService,
                                   SettingsService settingsService) {

        super(sender, sessionsService, new SearchTextInterceptor(sender, sessionsService, settingsService));
    }

    @Override
    protected void handleOnlyCommandInput(Message message) {
        sender.sendText(message.getChatId(), "Укажите текст для поиска:");
    }

    @Override
    public String getKey() {
        return "/set_search_text";
    }

    @Override
    public String getDescription() {
        return "Установить текст для поиска:";
    }
}
