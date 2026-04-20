package vacancy_tracker.services.telegram.command.settings;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.interceptors.MinSalaryInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class SetMinSalaryCommand extends InputInterceptingCommand {

    public SetMinSalaryCommand(MessageSender sender, SessionsService sersessionsServiceice,
                               SettingsService settingsService) {

        super(sender, sersessionsServiceice, new MinSalaryInterceptor(sender, sersessionsServiceice, settingsService));
    }

    @Override
    protected void handleOnlyCommandInput(Message message) {
        sender.sendText(message.getChatId(), "Укажите минимальное значение зарплаты:");
    }

    @Override
    public String getKey() {
        return "/set_min_salary";
    }

    @Override
    public String getDescription() {
        return "Установить минимальное значение зарплаты";
    }
}
