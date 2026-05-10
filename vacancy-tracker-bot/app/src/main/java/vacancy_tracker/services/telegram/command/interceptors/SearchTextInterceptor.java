package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class SearchTextInterceptor extends SettingInputInterceptor {

    public SearchTextInterceptor(MessageSender sender,
                                 SessionsService sessionsService,
                                 SettingsService settingsService) {
        super(sender, sessionsService, settingsService);
    }

    @Override
    protected boolean tryHandlePreparedInput(String text, long chatId) {
        var filters = settingsService.getFilters(chatId);
        filters.setText(text);
        settingsService.saveFilters(chatId, filters);
        return true;
    }
}
