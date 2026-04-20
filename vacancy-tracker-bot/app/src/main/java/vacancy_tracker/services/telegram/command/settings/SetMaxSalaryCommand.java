package vacancy_tracker.services.telegram.command.settings;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.interceptors.MaxSalaryInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class SetMaxSalaryCommand extends InputInterceptingCommand {

    public SetMaxSalaryCommand(MessageSender sender, SessionsService sessionsService,
                               SettingsService settingsService) {

        super(sender, sessionsService, new MaxSalaryInterceptor(sender, sessionsService, settingsService));
    }

    @Override
    protected void handleOnlyCommandInput(Message message) {
        sender.sendText(message.getChatId(), "Укажите максимальное значение зарплаты:");
    }

    @Override
    public String getKey() {
        return "/set_max_salary";
    }

    @Override
    public String getDescription() {
        return "Установить максимальное значение зарплаты";
    }
}
