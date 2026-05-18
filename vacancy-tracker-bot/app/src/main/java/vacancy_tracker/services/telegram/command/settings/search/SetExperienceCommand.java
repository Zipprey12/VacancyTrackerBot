package vacancy_tracker.services.telegram.command.settings.search;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.FloatInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.utils.DatesFormatUtil;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_EXPERIENCE;

@Component
public class SetExperienceCommand extends InputInterceptingCommand<Float> {

    public static final String KEY = "/set_experience";
    public static final String DESCRIPTION = "Установить опыт работы";
    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SearchFiltersService settingsService;

    public SetExperienceCommand(SendingAndUpdatingMessagePublisher publisher,
                                SessionsService sessionsService,
                                SearchFiltersService settingsService,
                                FiltersChangingCompletionHandler completionHandler) {

        super(KEY, DESCRIPTION, publisher,
                completionHandler,
                new FloatInterceptor(),
                sessionsService
        );

        this.settingsService = settingsService;
    }

    @Override
    protected void executeWithParameter(MessageData messageData, Float experience) {
        var chatId = messageData.getChatId();
        var filters = settingsService.get(chatId);
        if (experience < 0) {
            handleInvalidValue(messageData);
            return;
        }
        if (experience == 0) {
            filters.setExperience(null);
        } else {
            filters.setExperience(experience);
        }
        settingsService.save(chatId, filters);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var currentExperience = settingsService.get(messageData.getChatId()).getExperience();

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
                new CallbackItem(SET_EXPERIENCE.getKey(), "Нет опыта", 0),
                createItem(1),
                createItem(3),
                createItem(6),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущий")
        ), 2);
    }

    private static CallbackItem createItem(int experience) {
        return new CallbackItem(SET_EXPERIENCE.getKey(),
                DatesFormatUtil.formatYears(experience), experience);
    }
}
