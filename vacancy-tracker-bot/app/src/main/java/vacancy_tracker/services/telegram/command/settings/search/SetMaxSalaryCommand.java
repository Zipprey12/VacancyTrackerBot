package vacancy_tracker.services.telegram.command.settings.search;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.IntegerInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.utils.NumbersFormatUtil;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_MAX_SALARY;

@Component
public class SetMaxSalaryCommand extends InputInterceptingCommand<Integer> {

    public static final String KEY = "/set_max_salary";
    public static final String DESCRIPTION = "Установить максимальное значение зарплаты";
    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SearchFiltersService settingsService;

    protected SetMaxSalaryCommand(SendingAndUpdatingMessagePublisher publisher,
                                  SessionsService sessionsService,
                                  SearchFiltersService settingsService,
                                  FiltersChangingCompletionHandler completionHandler) {
        super(KEY, DESCRIPTION, publisher, completionHandler,
                new IntegerInterceptor(),
                sessionsService);

        this.settingsService = settingsService;
    }

    @Override
    protected void executeWithParameter(MessageData messageData, Integer value) {
        var chatId = messageData.getChatId();
        var filters = settingsService.get(chatId);
        var minSalary = filters.getMinSalary();

        if (value < 0) {
            handleInvalidValue(messageData, "Значение зарплаты не может быть отрицательным числом");
            return;
        }
        if (minSalary != null && value > minSalary) {
            handleInvalidValue(messageData, "Максимальная зарплата не может быть меньше минимальной");
            return;
        }

        filters.setMaxSalary(value);
        settingsService.save(chatId, filters);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var currentMaxSalary = settingsService.get(messageData.getChatId()).getMaxSalary();

        messageData.setText(createText(currentMaxSalary));
        messageData.setKeyboardMarkup(KEYBOARD);
    }

    private static String createText(Integer maxSalary) {
        String secondPart = "Чтобы *изменить:* выберите вариант из списка или отправьте число. " +
                "Например:  `150000`";

        return createHeader(maxSalary) + secondPart;
    }

    private static String createHeader(Integer maxSalary) {
        String value;
        if (maxSalary == null || maxSalary <= 0) {
            value = "не указано";
        } else {
            value = NumbersFormatUtil.formatNumber(maxSalary) + " ₽";
        }

        return "Текущее значение максимальной зарплаты: *" + value + "*\n";
    }

    private static InlineKeyboardMarkup initKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                createItem(60000),
                createItem(90000),
                createItem(125000),
                createItem(200000),
                new CallbackItem("0", SET_MAX_SALARY.getKey(), "Не указано"),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущий")
        ), 2);
    }

    private static CallbackItem createItem(int salary) {
        return new CallbackItem(SET_MAX_SALARY.getKey(),
                formatSalary(salary),
                salary);
    }

    private static String formatSalary(int salary) {
        var formatted = NumbersFormatUtil.formatNumber(salary);
        return formatted + " ₽";
    }
}