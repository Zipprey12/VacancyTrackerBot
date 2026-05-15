package vacancy_tracker.services.telegram.command.settings;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersInterceptingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.MinSalaryInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.utils.NumbersFormatUtil;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_FILTER_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_MIN_SALARY;

@Component
public class SetMinSalaryCommand extends InputInterceptingCommand {

    public static final String KEY = "/set_min_salary";
    public static final String DESCRIPTION = "Установить минимальное значение зарплаты";
    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SettingsService settingsService;

    protected SetMinSalaryCommand(SendingAndUpdatingMessagePublisher publisher,
                                  SessionsService sessionsService,
                                  SettingsService settingsService,
                                  FiltersInterceptingCompletionHandler completionHandler) {
        super(KEY, DESCRIPTION, publisher, completionHandler,
                new MinSalaryInterceptor(publisher.getSender(), sessionsService, settingsService),
                sessionsService);

        this.settingsService = settingsService;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var currentMinSalary = settingsService.getFilters(messageData.getChatId()).getMinSalary();

        messageData.setText(createText(currentMinSalary));
        messageData.setKeyboardMarkup(KEYBOARD);
    }

    private static String createText(Integer minSalary) {
        String secondPart = "Чтобы *изменить:* выберите вариант из списка или отправьте число. " +
                "Например:  `50000`";

        return createHeader(minSalary) + secondPart;
    }

    private static String createHeader(Integer minSalary) {
        String value;
        if (minSalary == null || minSalary <= 0) {
            value = "не указано";
        } else {
            value = NumbersFormatUtil.formatNumber(minSalary) + " ₽";
        }

        return "Текущее значение минимальной зарплаты: *" + value + "*\n";
    }

    private static InlineKeyboardMarkup initKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                createItem(40000),
                createItem(75000),
                createItem(100000),
                createItem(150000),
                new CallbackItem("0", SET_MIN_SALARY.getKey(), "Не указывать"),
                new CallbackItem(CANCEL_FILTER_CHANGE.getKey(), "Оставить текущий")
        ), 2);
    }

    private static CallbackItem createItem(int salary) {
        return new CallbackItem(String.valueOf(salary),
                SET_MIN_SALARY.getKey(),
                formatSalary(salary));
    }

    private static String formatSalary(int salary) {
        var formatted = NumbersFormatUtil.formatNumber(salary);
        return formatted + " ₽";
    }
}