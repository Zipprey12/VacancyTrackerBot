package vacancy_tracker.services.telegram.command.settings;

import lombok.AccessLevel;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public abstract class SettingInputInterceptor extends InputInterceptor {

    @Getter(AccessLevel.PROTECTED)
    private final SettingsService settingsService;

    private final MessageSender sender;

    protected SettingInputInterceptor(MessageSender sender, SessionsService sessionsService, SettingsService settingsService) {
        super(sessionsService);
        this.sender = sender;
        this.settingsService = settingsService;
    }

    protected abstract boolean tryHandleInput(String text, long chatId);

    @Override
    public void perform(Message message) {
        var text = message.getText();

        if (!tryHandleInput(text, message.getChatId())) {
            sender.sendInvalidValueError(message.getChatId());
        }
    }
}
