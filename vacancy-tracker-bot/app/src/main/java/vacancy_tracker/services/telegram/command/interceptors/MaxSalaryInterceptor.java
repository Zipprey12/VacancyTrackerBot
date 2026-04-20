package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.command.settings.SettingInputInterceptor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class MaxSalaryInterceptor extends SettingInputInterceptor {

    public MaxSalaryInterceptor(MessageSender sender, SessionsService sessionsService, SettingsService settingsService) {
        super(sender, sessionsService, settingsService);
    }

    @Override
    protected boolean tryHandleInput(String text, long chatId) {
        var value = StringUtil.parseInt(text);
        var filters = getSettingsService().getFilters(chatId);

        if (value.isEmpty()) {
            return false;
        }

        var maxSalary = value.get();
        var minSalary = filters.getMinSalary();

        if (maxSalary < 0 || (minSalary != null && minSalary > maxSalary)) {
            return false;
        }

        filters.setMaxSalary(value.get());
        getSettingsService().saveFilters(chatId, filters);
        return true;
    }
}
