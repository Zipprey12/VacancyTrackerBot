package vacancy_tracker.services.telegram.command.settings;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersInterceptingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.ExperienceInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.utils.DatesFormatUtil;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_FILTER_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_EXPERIENCE;

@Component
public class SetExperienceCommand extends InputInterceptingCommand {

    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SettingsService settingsService;

    public SetExperienceCommand(SendingAndUpdatingMessagePublisher publisher,
                                SessionsService sessionsService,
                                SettingsService settingsService,
                                FiltersInterceptingCompletionHandler completionHandler) {

        super("/set_experience", "Установить опыт работы", publisher,
                completionHandler,
                new ExperienceInterceptor(publisher.getSender(), sessionsService, settingsService),
                sessionsService
        );

        this.settingsService = settingsService;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var currentExperience = settingsService.getFilters(messageData.getChatId()).getExperience();

        messageData.setText(createText(currentExperience));
        messageData.setKeyboardMarkup(KEYBOARD);
    }

    private static String createText(Float experience) {
        var secondPart = "Чтобы *изменить:* выберите вариант из списка или отправьте число. " +
                "Например:  `1.5`";

        return createHeader(experience) + secondPart;
    }

    private static String createHeader(Float experience) {
        var value = (experience == null || experience <= 0) ? "отсутствует" : DatesFormatUtil.formatYears(experience);
        return "Текущее значение опыта: *" + value + "*\n";
    }

    private static InlineKeyboardMarkup initKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                new CallbackItem(String.valueOf(0), SET_EXPERIENCE.getKey(), "Нет опыта"),
                createItem(1),
                createItem(3),
                createItem(6),
                new CallbackItem(CANCEL_FILTER_CHANGE.getKey(), "Оставить текущий")
        ), 2);
    }

    private static CallbackItem createItem(int experience) {
        return new CallbackItem(String.valueOf(experience),
                SET_EXPERIENCE.getKey(),
                DatesFormatUtil.formatYears(experience));
    }
}
