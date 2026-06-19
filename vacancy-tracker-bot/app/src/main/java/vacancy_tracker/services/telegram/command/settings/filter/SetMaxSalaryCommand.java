package vacancy_tracker.services.telegram.command.settings.filter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.IntegerInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.SalaryFormatter;
import vacancy_tracker.services.telegram.view.utils.NumbersFormatUtil;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_MAX_SALARY;
import static vacancy_tracker.services.telegram.view.formatters.filter.SalaryFormatter.NEGATIVE_VALUE_MESSAGE;

@Component
public class SetMaxSalaryCommand extends InputInterceptingCommand<Integer> {

    public static final String KEY = "/set_max_salary";
    public static final String DESCRIPTION = "Установить максимальное значение зарплаты";

    private final InlineKeyboardMarkup keyboardMarkup = initKeyboard();

    private final SearchFiltersService settingsService;

    protected SetMaxSalaryCommand(SendingAndUpdatingMessagePublisher publisher,
                                  SessionsService sessionsService,
                                  SearchFiltersService settingsService,
                                  FiltersChangingCompletionHandler completionHandler,
                                  SequentialAsyncExecutionStrategy strategy) {
        super(new CommandArgs(KEY, DESCRIPTION, completionHandler), publisher,
                new IntegerInterceptor(), sessionsService, strategy);

        this.settingsService = settingsService;
    }

    @Override
    protected void executeWithParameters(MessageData messageData, Integer value) {
        var chatId = messageData.getChatId();
        var filters = settingsService.get(chatId);
        var minSalary = filters.getMinSalary();

        if (value < 0) {
            handleInvalidValue(messageData, NEGATIVE_VALUE_MESSAGE);
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
        messageData.setKeyboardMarkup(keyboardMarkup);
    }

    private static String createText(Integer maxSalary) {
        String secondPart = "Чтобы *изменить:* выберите вариант из списка или отправьте число. " +
                "Например:  `150 000`";

        return createHeader(maxSalary) + secondPart;
    }

    private static String createHeader(Integer maxSalary) {
        String value;
        if (maxSalary == null || maxSalary <= 0) {
            value = "не указано";
        } else {
            value = NumbersFormatUtil.formatSalary(maxSalary);
        }
        return "Текущее значение максимальной зарплаты: *" + value + "*\n";
    }

    private InlineKeyboardMarkup initKeyboard() {
        return SalaryFormatter.createKeyboard(SET_MAX_SALARY.getKey(), 60_000, 90_000, 125_000, 200_000);
    }
}