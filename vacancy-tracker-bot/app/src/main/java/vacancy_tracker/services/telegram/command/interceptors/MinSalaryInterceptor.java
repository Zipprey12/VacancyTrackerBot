package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class MinSalaryInterceptor extends SettingInputInterceptor {

    public MinSalaryInterceptor(MessageSender sender, SessionsService sessionsService,
                                SettingsService settingsService) {
        super(sender, sessionsService, settingsService);
    }

    @Override
    protected boolean tryHandlePreparedInput(String text, long chatId) {
        var value = StringUtil.parseInt(text);
        var filters = settingsService.getFilters(chatId);

        if (value.isEmpty()) {
            return false;
        }

        var minSalary = value.get();
        var maxSalary = filters.getMaxSalary();

        if (minSalary < 0 || (maxSalary != null && minSalary > maxSalary)) {
            return false;
        }

        filters.setMinSalary(minSalary);
        settingsService.saveFilters(chatId, filters);
        return true;
    }
}
