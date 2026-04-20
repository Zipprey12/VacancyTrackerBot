package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class SearchTextInterceptor extends SettingInputInterceptor {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    public SearchTextInterceptor(MessageSender sender,
                                 SessionsService sessionsService,
                                 SettingsService settingsService) {
        super(sender, sessionsService, settingsService);
    }

    @Override
    public boolean tryHandleInput(String text, long chatId) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String trimmed = text.trim();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            return false;
        }

        var filters = getSettingsService().getFilters(chatId);
        filters.setText(trimmed);
        getSettingsService().saveFilters(chatId, filters);
        return true;
    }
}
