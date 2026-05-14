package vacancy_tracker.services.telegram.command.settings;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.interceptors.MaxSalaryInterceptor;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.NumbersFormatUtil;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.*;

@Component
public class SetMaxSalaryCommand extends SearchFiltersCommand {

    public static final String KEY = "/set_max_salary";
    public static final String DESCRIPTION = "Установить максимальное значение зарплаты";
    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SettingsService settingsService;

    protected SetMaxSalaryCommand(MessageSender sender,
                                  MessageEditor editor,
                                  SessionsService sessionsService,
                                  ApplicationEventPublisher eventPublisher,
                                  SettingsService settingsService) {
        super(KEY, DESCRIPTION,
                sender,
                editor,
                sessionsService,
                new MaxSalaryInterceptor(sender, sessionsService, settingsService),
                eventPublisher);

        this.settingsService = settingsService;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var currentMaxSalary = settingsService.getFilters(messageData.getChatId()).getMaxSalary();

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
                new CallbackItem(CANCEL_FILTER_CHANGE.getKey(), "Оставить текущий")
        ), 2);
    }

    private static CallbackItem createItem(int salary) {
        return new CallbackItem(String.valueOf(salary),
                SET_MAX_SALARY.getKey(),
                formatSalary(salary));
    }

    private static String formatSalary(int salary) {
        var formatted = NumbersFormatUtil.formatNumber(salary);
        return formatted + " ₽";
    }
}