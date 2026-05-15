package vacancy_tracker.services.telegram.command.interceptors;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

//todo
public abstract class SettingInputInterceptor extends InputInterceptor {

    protected final SettingsService settingsService;
    protected final MessageSender sender;

    protected SettingInputInterceptor(MessageSender sender,
                                      SessionsService sessionsService,
                                      SettingsService settingsService) {
        super(sessionsService);
        this.sender = sender;
        this.settingsService = settingsService;
    }

    @Override
    public final void perform(Message message) {
        var text = message.getText();

        if (!tryHandleInput(text, message.getChatId())) {
            sender.sendInvalidValueError(message.getChatId());
        }
    }
}
