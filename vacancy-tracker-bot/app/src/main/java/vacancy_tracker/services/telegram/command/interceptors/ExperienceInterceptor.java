package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;

public class ExperienceInterceptor extends SettingInputInterceptor {

    public ExperienceInterceptor(MessageSender sender,
                                 SessionsService sessionsService,
                                 SettingsService settingsService) {
        super(sender, sessionsService, settingsService);
    }

    @Override
    protected boolean tryHandlePreparedInput(String text, long chatId) {
        var value = StringUtil.parseFloat(text);

        if (value.isEmpty()) {
            return false;
        }

        var filters = settingsService.getFilters(chatId);
        var experience = value.get();
        if (experience < 0) {
            return false;
        }
        if(experience == 0){
            filters.setExperienceFrom(null);
        }
        else {
            filters.setExperienceFrom(experience);
        }
        settingsService.saveFilters(chatId, filters);
        return true;
    }
}
