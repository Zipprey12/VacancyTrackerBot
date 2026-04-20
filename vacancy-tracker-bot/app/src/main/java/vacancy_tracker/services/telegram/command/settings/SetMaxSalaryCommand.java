package vacancy_tracker.services.telegram.command.settings;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.interceptors.MaxSalaryInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class SetMaxSalaryCommand extends InputInterceptingCommand {

    private final SessionsService service;

    public SetMaxSalaryCommand(MessageSender sender, SessionsService service, SettingsService settingsService) {

        super(sender, service, new MaxSalaryInterceptor(sender, service, settingsService));
        this.service = service;
    }

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();

        sender.sendText(chatId, "Введи макс зп");
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
