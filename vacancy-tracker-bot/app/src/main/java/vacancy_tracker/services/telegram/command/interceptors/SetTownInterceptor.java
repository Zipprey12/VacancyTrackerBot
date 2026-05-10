package vacancy_tracker.services.telegram.command.interceptors;

import org.springframework.stereotype.Component;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

//todo доделить
@Component
public class SetTownInterceptor extends SettingInputInterceptor{

    protected SetTownInterceptor(MessageSender sender, SessionsService sessionsService, SettingsService settingsService) {
        super(sender, sessionsService, settingsService);
    }

    @Override
    protected boolean tryHandlePreparedInput(String text, long chatId) {
        return false;
    }
}
